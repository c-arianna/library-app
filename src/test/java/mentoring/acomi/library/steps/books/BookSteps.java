package mentoring.acomi.library.steps.books;

import io.cucumber.docstring.DocString;
import io.cucumber.java.en.When;
import mentoring.acomi.library.common.TestConstants;
import mentoring.acomi.library.steps.Helper;
import mentoring.acomi.library.support.TestContext;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.test.web.server.LocalServerPort;

public class BookSteps {

	private final TestContext world;

	public BookSteps(TestContext world) {
		this.world = world;
	}

	@LocalServerPort
	int port;

	private RestTestClient client;

	/*
	 * ############################### WHEN #####################################
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
		rawFilters.forEach((k, v) -> filters.put(k, Helper.normalize(v)));

		String url = new StringBuilder().append(TestConstants.API_URL).append(port).toString();
		client = RestTestClient.bindToServer().baseUrl(url).build();

		UriComponentsBuilder uri = UriComponentsBuilder.fromPath("/books");
		filters.forEach(uri::queryParam);

		var result = client.get().uri(uri.build().toUri()).exchange().expectBody().returnResult();

		world.lastStatus = result.getStatus().value();
		world.lastBody = new String(result.getResponseBody(), StandardCharsets.UTF_8);
	}

	@When("l'amministratore aggiunge una copia del libro {string}, con i seguenti dati:")
	public void addBokCopy(String isbn, DocString body) {

		String url = new StringBuilder().append(TestConstants.API_URL).append(port).toString();
		client = RestTestClient.bindToServer().baseUrl(url).build();

		var result = client.post().uri(String.format("/books/%s/copies/add", isbn))
				.contentType(MediaType.APPLICATION_JSON).body(body.getContent()).exchange().expectBody().returnResult();

		world.lastStatus = result.getStatus().value();

		if (result.getResponseBody() != null) {
			world.lastBody = new String(result.getResponseBody(), StandardCharsets.UTF_8);
		}
	}

	@When("l'amministratore rimuove copie del libro {string}, con i seguenti dati:")
	public void removeVookCopies(String isbn, DocString body) {

		String url = new StringBuilder().append(TestConstants.API_URL).append(port).toString();
		client = RestTestClient.bindToServer().baseUrl(url).build();

		var result = client.post().uri(String.format("/books/%s/copies/remove", isbn))
				.contentType(MediaType.APPLICATION_JSON).body(body.getContent()).exchange().expectBody().returnResult();

		world.lastStatus = result.getStatus().value();

		if (result.getResponseBody() != null) {
			world.lastBody = new String(result.getResponseBody(), StandardCharsets.UTF_8);
		}
	}

}