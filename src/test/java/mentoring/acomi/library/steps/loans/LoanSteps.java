package mentoring.acomi.library.steps.loans;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
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
import tools.jackson.databind.ObjectMapper;
import mentoring.acomi.library.application.eventhandler.EventDispatcher;
import mentoring.acomi.library.application.repositories.BookViewRepository;
import mentoring.acomi.library.application.repositories.EventRepository;
import mentoring.acomi.library.application.repositories.LoanViewRepository;
import mentoring.acomi.library.application.repositories.UserViewRepository;
import mentoring.acomi.library.application.view.BookView;
import mentoring.acomi.library.application.view.LoanView;
import mentoring.acomi.library.application.view.UserView;
import mentoring.acomi.library.common.TestConstants;
import mentoring.acomi.library.domain.common.DateRange;
import mentoring.acomi.library.domain.events.LoanCanceledEvent;
import mentoring.acomi.library.domain.events.LoanConfirmedEvent;
import mentoring.acomi.library.domain.events.LoanRequestedEvent;
import mentoring.acomi.library.domain.events.LoanReservedEvent;
import mentoring.acomi.library.domain.events.payload.LoanPayload;
import mentoring.acomi.library.domain.events.payload.LoanRequestPayload;
import mentoring.acomi.library.domain.model.books.ISBN;
import mentoring.acomi.library.domain.model.loans.LoanStatus;
import mentoring.acomi.library.infrastructure.dto.loans.LoanResponse;
import mentoring.acomi.library.support.TestContext;

public class LoanSteps {

	private final UserViewRepository userViewRepository;
	private final TestContext world;
	private final EventRepository eventRepository;
	private final EventDispatcher dispatcher;
	private final LoanViewRepository loanViewRepository;
	private final BookViewRepository bookViewRepository;

	public LoanSteps(UserViewRepository userViewRepository, TestContext world, EventRepository eventRepository,
			EventDispatcher dispatcher, LoanViewRepository loanViewRepository, BookViewRepository bookViewRepository) {
		this.userViewRepository = userViewRepository;
		this.world = world;
		this.eventRepository = eventRepository;
		this.dispatcher = dispatcher;
		this.loanViewRepository = loanViewRepository;
		this.bookViewRepository = bookViewRepository;
	}

	@LocalServerPort
	int port;

	private RestTestClient client;

	/*
	 * ############################### GIVEN #####################################
	 */

	@Given("esiste l'utente {string}")
	public void addUser(String username) {
		String userId = UUID.randomUUID().toString();
		UserView user = new UserView(userId, username);
		userViewRepository.add(user);
		world.put("USER_ID", userId);
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
		String userId = world.get("USER_ID", String.class);
		String loanId = UUID.randomUUID().toString();
		DateRange period = new DateRange(LocalDate.now(), null);

		LoanRequestPayload payload = new LoanRequestPayload(loanId, ISBN.of(isbn).getValue(), userId, period,
				LoanStatus.PENDING);
		LoanRequestedEvent event = new LoanRequestedEvent(loanId, payload, Instant.now());
		eventRepository.appendToStream(event);
		dispatcher.dispatch(event);

		world.put("LOAN_ID", loanId);
	}
	
	@Given("esiste un prestito per il libro ISBN {string}, senza prenotazione del libro")
	public void addLoanWithoutReserve(String isbn) {
		String userId = world.get("USER_ID", String.class);
		String loanId = UUID.randomUUID().toString();
		String isbnValue = ISBN.of(isbn).getValue();
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

	@Given("il prestito con ID {string} non esiste")
	public void assertLoanNotExists(String loanId) {
		// given dichiarativo, non contiene implementazione
		world.put("LOAN_ID", loanId);
	}

	@Given("il prestito del libro {string} è stato annullato")
	public void cancelLoan(String isbn) {
		isbn = ISBN.of(isbn).getValue();
		String userId = world.get("USER_ID", String.class);
		String loanId = world.get("LOAN_ID", String.class);

		if (loanId == null) {
			throw new AssertionError(String.format("Loan ID not found in context world"));
		}

		LoanPayload payload = new LoanPayload(loanId, isbn, userId);
		LoanCanceledEvent event = new LoanCanceledEvent(loanId, payload, Instant.now());
		eventRepository.appendToStream(event);
		dispatcher.dispatch(event);

	}

	@Given("il prestito del libro {string} è stato confermato")
	public void confirmLoan(String isbn) {
		isbn = ISBN.of(isbn).getValue();
		String userId = world.get("USER_ID", String.class);
		String loanId = world.get("LOAN_ID", String.class);

		if (loanId == null) {
			throw new AssertionError(String.format("Loan ID not found in context world"));
		}

		LoanPayload payload = new LoanPayload(loanId, isbn, userId);
		LoanConfirmedEvent event = new LoanConfirmedEvent(loanId, payload, Instant.now());
		eventRepository.appendToStream(event);
		dispatcher.dispatch(event);

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
	 * ############################### WHEN #####################################
	 */
	
	@Then("il prestito nel read model ha stato {string}")
	public void checkLoanStatus(String status) {
		
		String loanId = world.get("LOAN_ID", String.class);

		if (loanId == null) {
			throw new AssertionError(String.format("Loan ID not found in context world"));
		}
		
		Optional<LoanView> loanView = loanViewRepository.findById(loanId);
		
		if(loanView.isEmpty()) {
			throw new AssertionError(String.format("Loan not found, ID: %s", loanId));
		}
		
		Assertions.assertEquals(LoanStatus.valueOf(status), loanView.get().status());
		
	}
	
	@Then("il libro {string} ha totalCopies = {int}, borrowedCopies = {int}, availableCopies = {int}, reservedCopies = {int}")
	public void checkBookView(String isbn, int totalCopies, int borrowedCopies, int availableCopies, int reservedCopies) {
		
		Optional<BookView> bookView = bookViewRepository.findById(isbn);
		
		if(bookView.isEmpty()) {
			throw new AssertionError(String.format("Book not found, ISBN: %s", isbn));
		}
		
		Assertions.assertEquals(totalCopies, bookView.get().totalCopies());
		Assertions.assertEquals(borrowedCopies, bookView.get().borrowedCopies());
		Assertions.assertEquals(availableCopies, bookView.get().availableCopies());
		Assertions.assertEquals(reservedCopies, bookView.get().reservedCopies());
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

}
