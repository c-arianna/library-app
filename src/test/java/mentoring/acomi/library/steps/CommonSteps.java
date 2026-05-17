package mentoring.acomi.library.steps;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;

import com.jayway.jsonpath.JsonPath;

import io.cucumber.docstring.DocString;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import mentoring.acomi.library.application.repositories.EventRepository;
import mentoring.acomi.library.application.services.BookService;
import mentoring.acomi.library.domain.events.DomainEvent;
import mentoring.acomi.library.infrastructure.dto.books.AddBookCopiesRequest;
import mentoring.acomi.library.infrastructure.dto.books.AddBookRequest;
import mentoring.acomi.library.support.ExpectedValue;
import mentoring.acomi.library.support.TestContext;

public class CommonSteps {

	private final BookService service;
	private final TestContext world;
	private final EventRepository repository;

	public CommonSteps(BookService service, TestContext world, EventRepository repository) {
		this.service = service;
		this.world = world;
		this.repository = repository;
	}

	/*
	 * ############################### GIVEN #####################################
	 */

	@Given("l'amministratore aggiunge un libro con isbn {string}, autore {string}, titolo {string} e descrizione")
	public void addBook(String isbn, String author, String title, DocString description) {

		String bookDescription = description.getContent().trim();

		AddBookRequest request = new AddBookRequest(isbn, author, title, bookDescription);

		service.addBook(request);
	}

	@Given("l'amministratore aggiunge {int} copie del libro {string}")
	public void addCopies(int quantity, String isnb) {
		service.addBookCopies(new AddBookCopiesRequest(quantity), isnb);
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
