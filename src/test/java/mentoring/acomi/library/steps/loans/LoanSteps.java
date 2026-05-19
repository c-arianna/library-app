package mentoring.acomi.library.steps.loans;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import io.cucumber.docstring.DocString;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.messages.ndjson.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.messages.ndjson.internal.com.fasterxml.jackson.databind.JsonMappingException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import mentoring.acomi.library.application.aggregates.AggregateFactory;
import mentoring.acomi.library.application.aggregates.LoanAggregate;
import mentoring.acomi.library.application.repositories.EventRepository;
import mentoring.acomi.library.application.repositories.LoanViewRepository;
import mentoring.acomi.library.application.repositories.UserViewRepository;
import mentoring.acomi.library.application.view.LoanView;
import mentoring.acomi.library.application.view.UserView;
import mentoring.acomi.library.common.TestConstants;
import mentoring.acomi.library.domain.common.DateRange;
import mentoring.acomi.library.domain.events.DomainEvent;
import mentoring.acomi.library.domain.events.LoanRequestedEvent;
import mentoring.acomi.library.domain.events.LoanReservedEvent;
import mentoring.acomi.library.domain.events.payload.LoanPayload;
import mentoring.acomi.library.domain.events.payload.LoanRequestPayload;
import mentoring.acomi.library.domain.model.books.ISBN;
import mentoring.acomi.library.domain.model.loans.Loan;
import mentoring.acomi.library.domain.model.loans.LoanIdentifier;
import mentoring.acomi.library.domain.model.loans.LoanStatus;
import mentoring.acomi.library.infrastructure.dto.loans.LoanResponse;
import mentoring.acomi.library.steps.Helper;
import mentoring.acomi.library.support.ExpectedValue;
import mentoring.acomi.library.support.TestContext;

public class LoanSteps {

	private final UserViewRepository userViewRepository;
	private final TestContext world;
	private final EventRepository eventRepository;
	private final LoanViewRepository loanViewRepository;
	private final AggregateFactory aggregateFactory;

	public LoanSteps(UserViewRepository userViewRepository, TestContext world, EventRepository eventRepository,
			LoanViewRepository loanViewRepository, AggregateFactory aggregateFactory) {
		this.userViewRepository = userViewRepository;
		this.world = world;
		this.eventRepository = eventRepository;
		this.loanViewRepository = loanViewRepository;
		this.aggregateFactory = aggregateFactory;
	}

	@LocalServerPort
	int port;

	private RestTestClient client;

	/*
	 * ############################### GIVEN #####################################
	 */

	@Given("esiste l'utente {string}")
	public void addUser(String username) {
		gievnUserAdded(username);
	}

	@Given("il catalogo non contiene il libro con isbn {string}")
	public void assertBookNotExists(String isbn) {
		// given dichiarativo, non contiene implementazione
	}

	@Given("l'utente con ID {string} non esiste")
	public void assertUserNotExists(String userId) {
		// given dichiarativo, non contiene implementazione
	}

	@Given("esiste un prestito per il libro ISBN {string} in attesa di conferma")
	public void addLoan(String isbn) {
		givenLoanAdded(isbn);
	}

	@Given("esiste un prestito per il libro ISBN {string}, senza prenotazione del libro")
	public void addLoanWithoutReserve(String isbn) {
		givenLoanWithoutReservationAdded(isbn);
	}

	@Given("il prestito con ID {string} non esiste")
	public void assertLoanNotExists(String loanId) {
		// given dichiarativo, non contiene implementazione
		world.put("LOAN_ID", loanId);
	}

	@Given("il prestito del libro {string} è stato annullato")
	public void cancelLoan(String isbn) {
		givenLoanCanceled();
	}

	@Given("il prestito del libro {string} è stato confermato")
	public void confirmLoan(String isbn) {
		givenLoanConfirmed();
	}

	/*
	 * ############################### WHEN #####################################
	 */

	@When("l'utente crea una richiesta di prestito con i seguenti dati:")
	public void createLoan(DocString body) throws JsonMappingException, JsonProcessingException {

		String request = resolveDocString(body.getContent());

		String url = new StringBuilder().append(TestConstants.API_URL).append(port).toString();
		client = RestTestClient.bindToServer().baseUrl(url).build();

		var result = client.post().uri("/loans").contentType(MediaType.APPLICATION_JSON).body(request).exchange()
				.expectBody().returnResult();

		world.lastStatus = result.getStatus().value();
		world.lastBody = new String(result.getResponseBody(), StandardCharsets.UTF_8);

		if (result.getStatus().is2xxSuccessful()) {
			ObjectMapper mapper = new ObjectMapper();
			LoanResponse response = mapper.readValue(world.lastBody, LoanResponse.class);

			world.put("LOAN_ID", response.loanId());
		}

	}

	@When("l'amministratore conferma la richiesta del prestito")
	public void confirmLoan() {

		String url = new StringBuilder().append(TestConstants.API_URL).append(port).toString();
		client = RestTestClient.bindToServer().baseUrl(url).build();

		String loanId = world.get("LOAN_ID", String.class);

		if (loanId == null) {
			throw new AssertionError(String.format("Loan ID not found in context world"));
		}

		String uri = String.format("/loans/%s/confirm", loanId);
		var result = client.post().uri(uri).contentType(MediaType.APPLICATION_JSON).exchange().expectBody()
				.returnResult();

		world.lastStatus = result.getStatus().value();
		if (result.getResponseBody() != null) {
			world.lastBody = new String(result.getResponseBody(), StandardCharsets.UTF_8);
		}

	}

	@When("l'amministratore annulla la richiesta del prestito")
	public void cancelLoan() {

		String url = new StringBuilder().append(TestConstants.API_URL).append(port).toString();
		client = RestTestClient.bindToServer().baseUrl(url).build();

		String loanId = world.get("LOAN_ID", String.class);

		if (loanId == null) {
			throw new AssertionError(String.format("Loan ID not found in context world"));
		}

		String uri = String.format("/loans/%s/reject", loanId);
		var result = client.post().uri(uri).contentType(MediaType.APPLICATION_JSON).exchange().expectBody()
				.returnResult();

		world.lastStatus = result.getStatus().value();
		if (result.getResponseBody() != null) {
			world.lastBody = new String(result.getResponseBody(), StandardCharsets.UTF_8);
		}

	}

	@When("l'amministratore esegue l'operazione di reso del prestito")
	public void returnLoan() {

		String url = new StringBuilder().append(TestConstants.API_URL).append(port).toString();
		client = RestTestClient.bindToServer().baseUrl(url).build();

		String loanId = world.get("LOAN_ID", String.class);

		if (loanId == null) {
			throw new AssertionError(String.format("Loan ID not found in context world"));
		}

		String uri = String.format("/loans/%s/return", loanId);
		var result = client.post().uri(uri).contentType(MediaType.APPLICATION_JSON).exchange().expectBody()
				.returnResult();

		world.lastStatus = result.getStatus().value();
		if (result.getResponseBody() != null) {
			world.lastBody = new String(result.getResponseBody(), StandardCharsets.UTF_8);
		}

	}

	/*
	 * ############################### THEN #####################################
	 */

	@Then("il prestito nel read model ha stato {string}")
	public void checkLoanStatus(String status) {

		String loanId = world.get("LOAN_ID", String.class);

		if (loanId == null) {
			throw new AssertionError(String.format("Loan ID not found in context world"));
		}

		Optional<LoanView> loanView = loanViewRepository.findById(loanId);

		if (loanView.isEmpty()) {
			throw new AssertionError(String.format("Loan not found, ID: %s", loanId));
		}

		Assertions.assertEquals(LoanStatus.valueOf(status), loanView.get().status());

	}

	@Then("è stato generato l'evento {string} con aggregateId {string} e payload:")
	public void checkEventPayload(String eventType, String aggregateId, Map<String, String> expectedRaw) {

		aggregateId = Helper.resolve(aggregateId, world);
		Optional<DomainEvent> event = eventRepository.getEvent(eventType, aggregateId);

		Assertions.assertTrue(event.isPresent(),
				String.format("Event %s not found for aggregate ID %s", eventType, aggregateId));

		Map<String, ExpectedValue> expectedPayload = new LinkedHashMap<>();
		expectedRaw.forEach((k, v) -> {
			String resolved = Helper.resolve(v, world);
			expectedPayload.put(k, Helper.normalizeExpected(resolved));
		});

		Object payload = event.get().payload();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> actualPayload = mapper.convertValue(payload,
				mapper.getTypeFactory().constructType(new TypeReference<Map<String, Object>>() {
				}));

		expectedPayload.forEach((key, expectedValue) -> {

			Object actualValue = actualPayload.get(key);

			Assertions.assertNotNull(actualValue, String.format("Missing field in payload: %s", key));
			Assertions.assertTrue(expectedValue.matches(actualValue), String.format("Mismatch on field: %s", key));

		});

	}
	
	/*
	 * ############################### HELPER #####################################
	 */

	private void gievnUserAdded(String username) {
		String userId = generateId();
		UserView user = new UserView(userId, username);
		userViewRepository.add(user);
		world.put("USER_ID", userId);
	}

	private void givenLoanAdded(String isbn) {
		
		String userId = world.get("USER_ID", String.class);
		String loanId = generateId();

		LoanAggregate loanAggregate = aggregateFactory.loadLoan(loanId);
		Loan loan = Loan.create(new LoanIdentifier(loanId), isbn(isbn), userId, LocalDate.now(), null);

		loanAggregate.add(loan);

		world.put("LOAN_ID", loanId);
	}

	private void givenLoanWithoutReservationAdded(String isbn) {
		
		String userId = world.get("USER_ID", String.class);
		String loanId = generateId();
		String isbnValue = isbn(isbn);
		DateRange period = new DateRange(LocalDate.now(), null);

		LoanRequestPayload payload = new LoanRequestPayload(loanId, isbnValue, userId, period, LoanStatus.PENDING);
		LoanRequestedEvent requestedEvent = new LoanRequestedEvent(loanId, payload, Instant.now());
		eventRepository.appendToStream(requestedEvent);

		LoanPayload reservedPayload = new LoanPayload(loanId, isbnValue, userId);
		LoanReservedEvent reservedEvent = new LoanReservedEvent(loanId, reservedPayload, Instant.now());
		eventRepository.appendToStream(reservedEvent);

		loanViewRepository.insertRequest(new LoanView(loanId, isbnValue, userId, period.getStart(), period.getEnd(), LoanStatus.RESERVED));

		world.put("LOAN_ID", loanId);
	}

	private void givenLoanCanceled() throws AssertionError {
		String loanId = world.get("LOAN_ID", String.class);

		if (loanId == null) {
			throw new AssertionError(String.format("Loan ID not found in context world"));
		}

		LoanAggregate loan = aggregateFactory.loadLoan(loanId);
		loan.cancel();
	}

	private void givenLoanConfirmed() throws AssertionError {
		String loanId = world.get("LOAN_ID", String.class);

		if (loanId == null) {
			throw new AssertionError(String.format("Loan ID not found in context world"));
		}

		LoanAggregate loan = aggregateFactory.loadLoan(loanId);

		loan.confirm();
	}
	
	private String resolveDocString(String docString) {

		if (docString == null) {
			return null;
		}

		String result = docString;

		for (String key : world.keys()) {
			String placeholder = String.format("${%s}", key);
			String value = world.get(key, String.class);

			if (value == null) {
				throw new AssertionError(String.format("Placeholder not resolved: ${%s}", key));
			}

			result = result.replace(placeholder, value);
		}

		return result;

	}
	
	private String generateId() {
		return UUID.randomUUID().toString();
	}
	
	private String isbn(String isbn) {
		return ISBN.of(isbn).getValue();
	}

}
