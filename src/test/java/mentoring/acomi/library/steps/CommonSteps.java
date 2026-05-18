package mentoring.acomi.library.steps;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;

import com.jayway.jsonpath.JsonPath;

import io.cucumber.docstring.DocString;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import mentoring.acomi.library.application.eventhandler.EventDispatcher;
import mentoring.acomi.library.application.repositories.EventRepository;
import mentoring.acomi.library.domain.events.BookBorrowedEvent;
import mentoring.acomi.library.domain.events.BookCopiesAddedEvent;
import mentoring.acomi.library.domain.events.BookCopiesRemovedEvent;
import mentoring.acomi.library.domain.events.BookRegisteredEvent;
import mentoring.acomi.library.domain.events.DomainEvent;
import mentoring.acomi.library.domain.events.payload.BookCopiesAddedPayload;
import mentoring.acomi.library.domain.events.payload.BookCopiesRemovedPayload;
import mentoring.acomi.library.domain.events.payload.BookLoanPayload;
import mentoring.acomi.library.domain.events.payload.BookRegisteredPayload;
import mentoring.acomi.library.domain.model.books.ISBN;
import mentoring.acomi.library.support.ExpectedValue;
import mentoring.acomi.library.support.TestContext;

public class CommonSteps {
	
	private final TestContext world;
	private final EventRepository repository;
	private final EventDispatcher dispatcher;
    
	public CommonSteps(TestContext world, EventRepository repository, EventDispatcher dispatcher) {
		this.world = world;
		this.repository = repository;
		this.dispatcher = dispatcher;
	}

	/*
	 * ############################### GIVEN #####################################
	 */

	@Given("l'amministratore aggiunge un libro con isbn {string}, autore {string}, titolo {string} e descrizione")
	public void addBook(String isbn, String author, String title, DocString description) {

		String bookDescription = description.getContent().trim();
		isbn = ISBN.of(isbn).getValue();
		BookRegisteredPayload payload = new BookRegisteredPayload(isbn, author, title, bookDescription);
		BookRegisteredEvent event = new BookRegisteredEvent(isbn, payload, Instant.now());
		repository.appendToStream(event);
		dispatcher.dispatch(event);
	}

	@Given("l'amministratore aggiunge {int} copie del libro {string}")
	public void addCopies(int quantity, String isbn) {
		BookCopiesAddedPayload payload = new BookCopiesAddedPayload(isbn, quantity);
		BookCopiesAddedEvent event = new BookCopiesAddedEvent(isbn, payload, Instant.now());
		repository.appendToStream(event);
		dispatcher.dispatch(event);
	}
	
	@Given("una copia del libro {string} è in stato borrowed")
	public void borrowBook(String isbn) {
		String userId = UUID.randomUUID().toString();
		String loanId = UUID.randomUUID().toString();
		isbn = ISBN.of(isbn).getValue();
		BookLoanPayload payload = new BookLoanPayload(isbn, loanId, userId);
		BookBorrowedEvent event = new BookBorrowedEvent(isbn, payload, Instant.now());
		repository.appendToStream(event);
		dispatcher.dispatch(event);
	}
	
	@Given("una copia del libro {string} è stata rimossa")
	public void removeBookCopy(String isbn) {
		isbn = ISBN.of(isbn).getValue();
		BookCopiesRemovedPayload payload = new BookCopiesRemovedPayload(isbn, 1, "Copy Lost");
		BookCopiesRemovedEvent event = new BookCopiesRemovedEvent(isbn, payload, Instant.now());
		repository.appendToStream(event);
		dispatcher.dispatch(event);
	}
	
	/*
	 * ############################### THEN #####################################
	 */

	@Then("la risposta ha status code {int}")
	public void checkResponseStatusCode(int status) {
		Assertions.assertEquals(status, this.world.lastStatus);
	}

	@Then("la risposta contiene il campo {string}")
	public void checkResponseField(String field) {
		var context = JsonPath.parse(world.lastBody);
		Object value = context.read(String.format("$.%s", field));
		Assertions.assertNotNull(value, String.format("Missing field: %s", field));
	}
	
	@Then("è stato generato l'evento {string} con aggregateId {string} e payload:")
	public void checkEventPayload(String eventType, String aggregateId, Map<String, String> expectedRaw) {

		aggregateId = resolve(aggregateId);
		Optional<DomainEvent> event = repository.getEvent(eventType, aggregateId);

		Assertions.assertTrue(event.isPresent(), String.format("Event %s not found for aggregate ID %s", eventType, aggregateId));

		Map<String, ExpectedValue> expectedPayload = new LinkedHashMap<>();
		expectedRaw.forEach((k, v) -> {
			String resolved = resolve(v);
			expectedPayload.put(k, normalizeExpected(resolved));
		});

		Object payload = event.get().payload();
		ObjectMapper mapper = new ObjectMapper();
	
		Map<String, Object> actualPayload = mapper.convertValue(payload, 
				mapper.getTypeFactory().constructType(new TypeReference<Map<String, Object>>() {}));

		expectedPayload.forEach((key, expectedValue) -> {

			Object actualValue = actualPayload.get(key);

			Assertions.assertNotNull(actualValue, String.format("Missing field in payload: %s", key));
			Assertions.assertTrue(expectedValue.matches(actualValue), String.format("Mismatch on field: %s", key));

		});

	}

	@Then("la risposta contiene i seguenti campi:")
	public void checkResponseContent(Map<String, String> expectedRaw) {
		
		var context = JsonPath.parse(world.lastBody);
			
		Map<String, ExpectedValue> expectedContent = new LinkedHashMap<>();
		expectedRaw.forEach((k, v) -> {
			String resolved = resolve(v);
			expectedContent.put(k, normalizeExpected(resolved));
		});
		
		expectedContent.forEach((key, expectedValue) -> {
			Object actualValue = context.read(String.format("$.%s", key));
			Assertions.assertNotNull(actualValue, String.format("Missing field in response: %s", key));
			Assertions.assertTrue(expectedValue.matches(actualValue), String.format("Mismatch on field: %s, actual value: %s", key, actualValue));
		});
		
		
	}
	
	private String resolve(String value) {
		if (value.startsWith("${") && value.endsWith("}")) {
			String key = value.substring(2, value.length() - 1);
			return world.get(key, String.class); 
		}
		return value;
	}

	private static ExpectedValue normalizeExpected(String raw) {

		if (raw == null) {
			return ExpectedValue.empty();
		}

		String value = raw.trim();

		if (value.equalsIgnoreCase("EMPTY")) {
			return ExpectedValue.empty();
		}

		if (value.equalsIgnoreCase("NULL")) {
			return ExpectedValue.nullValue();
		}

		if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
			value = value.substring(1, value.length() - 1);
			return ExpectedValue.ofString(value);
		}

		if (value.matches("[-+]?\\d+(\\.\\d+)?")) {
			return ExpectedValue.ofNumber(new BigDecimal(value));
		}

		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
			return ExpectedValue.ofBoolean(Boolean.parseBoolean(value));
		}

		return ExpectedValue.ofString(value);
	}

}
