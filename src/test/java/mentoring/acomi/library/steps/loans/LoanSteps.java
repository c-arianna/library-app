package mentoring.acomi.library.steps.loans;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import io.cucumber.docstring.DocString;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.messages.ndjson.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.messages.ndjson.internal.com.fasterxml.jackson.databind.JsonMappingException;
import tools.jackson.databind.ObjectMapper;
import mentoring.acomi.library.application.repositories.UserViewRepository;
import mentoring.acomi.library.application.view.UserView;
import mentoring.acomi.library.common.TestConstants;
import mentoring.acomi.library.infrastructure.dto.loans.LoanResponse;
import mentoring.acomi.library.support.TestContext;

public class LoanSteps {

	private final UserViewRepository repository;
	private final TestContext world;

	public LoanSteps(UserViewRepository repository, TestContext world) {
		this.repository = repository;
		this.world = world;
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
		repository.add(user);
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
