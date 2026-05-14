package mentoring.acomi.library.steps;

import io.cucumber.docstring.DocString;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import mentoring.acomi.library.application.repositories.EventRepository;
import mentoring.acomi.library.application.services.BookService;
import mentoring.acomi.library.common.TestConstants;
import mentoring.acomi.library.infrastructure.dto.books.AddBookRequest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.boot.test.web.server.LocalServerPort;

public class BookSteps {

	private final BookService service;
	private final EventRepository eventRepository;
	
	public BookSteps(BookService service, EventRepository eventRepository) {
		this.service = service;
		this.eventRepository = eventRepository;
	}
	
	@LocalServerPort
	int port;

	private RestTestClient client;
	private RestTestClient.ResponseSpec lastResponse;

	@Given("aggiungo un libro con isbn {string}, autore {string}, titolo {string} e descrizione")
	public void addBook(String isbn, String author, String title, DocString description) {

		String bookDescription = description.getContent().trim();
		
		AddBookRequest request = new AddBookRequest(isbn, author, title, bookDescription);
		
		service.addBook(request);
	}

	@When("l'amministratore aggiunge un libro al catalogo con i seguenti dati:")
	public void createBook(DocString body) {

		String url = new StringBuilder().append(TestConstants.API_URL).append(port).toString();
		client = RestTestClient.bindToServer().baseUrl(url).build();

		lastResponse = client.post().uri("/books").contentType(MediaType.APPLICATION_JSON).body(body.getContent())
				.exchange();

	}

	@Then("la risposta ha status code {int}")
	public void checkResponseStatusCode(int status) {
		lastResponse.expectStatus().isEqualTo(status);
	}

	@And("la risposta contiene il campo {string}")
	public void checkResponseField(String field) {
		lastResponse.expectBody().jsonPath(String.join("", "$.", field)).exists();
	}
	
	@And("è stato generato l'evento {string} con aggregateId {string}")
	public void checkEvent(String eventType, String aggregateId) {
		eventRepository.existsEvent(eventType, aggregateId);
	}
}