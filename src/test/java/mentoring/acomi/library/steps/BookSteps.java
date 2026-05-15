package mentoring.acomi.library.steps;

import io.cucumber.docstring.DocString;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import mentoring.acomi.library.application.repositories.EventRepository;
import mentoring.acomi.library.application.services.BookService;
import mentoring.acomi.library.common.TestConstants;
import mentoring.acomi.library.infrastructure.dto.books.AddBookRequest;
import mentoring.acomi.library.support.TestContext;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.jayway.jsonpath.JsonPath;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.server.LocalServerPort;

import mentoring.acomi.library.support.ExpectedValue;

public class BookSteps {

	private final BookService service;
	private final EventRepository eventRepository;
	private final TestContext world;

	public BookSteps(BookService service, EventRepository eventRepository, TestContext world) {
		this.service = service;
		this.eventRepository = eventRepository;
		this.world = world;
	}

	@LocalServerPort
	int port;

	private RestTestClient client;

	/* 
	 *  ############################### GIVEN #####################################
	 */
	
	@Given("l'amministratore aggiunge un libro con isbn {string}, autore {string}, titolo {string} e descrizione")
	public void addBook(String isbn, String author, String title, DocString description) {

		String bookDescription = description.getContent().trim();

		AddBookRequest request = new AddBookRequest(isbn, author, title, bookDescription);

		service.addBook(request);
	}

	/* 
	 *  ############################### WHEN #####################################
	 */
	
	@When("l'amministratore aggiunge un libro al catalogo con i seguenti dati:")
	public void createBook(DocString body) {

		String url = new StringBuilder().append(TestConstants.API_URL).append(port).toString();
		client = RestTestClient.bindToServer().baseUrl(url).build();

		var result = client.post().uri("/books").contentType(MediaType.APPLICATION_JSON).body(body.getContent())
				.exchange().expectBody().returnResult();

		world.lastStatus = result.getStatus().value();
		world.lastBody = new String(result.getResponseBody(), StandardCharsets.UTF_8);

	}

	@When("l'utente visualizza il catalogo dei libri")
	public void findBooks() {

		String url = new StringBuilder().append(TestConstants.API_URL).append(port).toString();
		client = RestTestClient.bindToServer().baseUrl(url).build();

		var result = client.get().uri("/books").exchange().expectBody().returnResult();

		world.lastStatus = result.getStatus().value();
		world.lastBody = new String(result.getResponseBody(), StandardCharsets.UTF_8);
	}

	@When("l'utente visualizza il catalogo dei libri, con filtro di ricerca")
	public void findBooksFilter(Map<String, String> rawFilters) {

		Map<String, String> filters = new LinkedHashMap<>();
		rawFilters.forEach((k, v) -> filters.put(k, normalize(v)));

		String url = new StringBuilder().append(TestConstants.API_URL).append(port).toString();
		client = RestTestClient.bindToServer().baseUrl(url).build();

		UriComponentsBuilder uri = UriComponentsBuilder.fromPath("/books");
		filters.forEach(uri::queryParam);

		var result = client.get().uri(uri.build().toUri()).exchange().expectBody().returnResult();

		world.lastStatus = result.getStatus().value();
		world.lastBody = new String(result.getResponseBody(), StandardCharsets.UTF_8);
	}

	@When("l'amministratore aggiunge una copia del libro, con i seguenti dati:")
	public void addBokCopy(DocString body) {

		String url = new StringBuilder().append(TestConstants.API_URL).append(port).toString();
		client = RestTestClient.bindToServer().baseUrl(url).build();

		var result = client.post().uri("/books/add/bookcopy").contentType(MediaType.APPLICATION_JSON).body(body.getContent())
				.exchange().expectBody().returnResult();

		world.lastStatus = result.getStatus().value();
		world.lastBody = new String(result.getResponseBody(), StandardCharsets.UTF_8);

	}
	
	/* 
	 *  ############################### THEN #####################################
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

	@Then("è stato generato l'evento {string} con aggregateId {string}")
	public void checkEvent(String eventType, String aggregateId) {
		Assertions.assertTrue(eventRepository.existsEvent(eventType, aggregateId));
	}

	@Then("{string} è una lista vuota")
	public void checkEmptyList(String field) {
		var context = JsonPath.parse(world.lastBody);
		Integer size = context.read(String.format("$.%s.length()", field));
		Assertions.assertEquals(0, size, String.format("'%s' is not empty", field));
	}

	@Then("{string} contiene {int} elementi")
	public void checkList(String field, int size) {
		var context = JsonPath.parse(world.lastBody);
		Integer actualSize = context.read(String.format("$.%s.length()", field));
		Assertions.assertEquals(size, actualSize,
				String.format("Expected %d elements in '%s', found %d", size, field, actualSize));
	}

	@Then("{string} ha un elemento con i campi:")
	public void checkContentList(String field, Map<String, String> expectedRaw) {

		Map<String, ExpectedValue> expected = new LinkedHashMap<>();
		expectedRaw.forEach((k, v) -> expected.put(k, normalizeExpected(v)));

		var context = JsonPath.parse(world.lastBody);
		List<Map<String, Object>> items = context.read(String.format("$.%s", field));

		boolean found = items.stream().anyMatch(
				item -> expected.entrySet().stream().allMatch(e -> e.getValue().matches(item.get(e.getKey()))));

		Assertions.assertTrue(found,
				String.format("No items in the '%s' field match the expected values: %s", field, expected));

	}

	private static String normalize(String value) {
		
		if (value == null)
			return "";

		value = value.trim();

		if (value.equals("\"\""))
			return "";

		if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
			return value.substring(1, value.length() - 1);
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