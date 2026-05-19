package mentoring.acomi.library.steps;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;

import com.jayway.jsonpath.JsonPath;

import io.cucumber.docstring.DocString;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import mentoring.acomi.library.application.aggregates.AggregateFactory;
import mentoring.acomi.library.application.aggregates.BookAggregate;
import mentoring.acomi.library.application.aggregates.LoanAggregate;
import mentoring.acomi.library.application.repositories.BookViewRepository;
import mentoring.acomi.library.application.view.BookView;
import mentoring.acomi.library.domain.model.books.Book;
import mentoring.acomi.library.domain.model.books.ISBN;
import mentoring.acomi.library.domain.model.loans.Loan;
import mentoring.acomi.library.domain.model.loans.LoanIdentifier;
import mentoring.acomi.library.support.ExpectedValue;
import mentoring.acomi.library.support.TestContext;

public class CommonSteps {

	private final TestContext world;
	private final BookViewRepository bookViewRepository;
	private final AggregateFactory aggregateFactory;

	public CommonSteps(TestContext world, BookViewRepository bookViewRepository, AggregateFactory aggregateFactory) {
		this.world = world;
		this.bookViewRepository = bookViewRepository;
		this.aggregateFactory = aggregateFactory;
	}

	/*
	 * ############################### GIVEN #####################################
	 */

	@Given("l'amministratore aggiunge un libro con isbn {string}, autore {string}, titolo {string} e descrizione")
	public void addBook(String isbn, String author, String title, DocString description) {
		givenBookRegistered(isbn, author, title, description);
	}

	@Given("l'amministratore aggiunge {int} copie del libro {string}")
	public void addCopies(int quantity, String isbn) {
		givenCopiesAdded(quantity, isbn);
	}

	@Given("una copia del libro {string} è in stato borrowed")
	public void borrowBook(String isbn) {
		givenBookBorrowed(isbn);
	}

	@Given("una copia del libro {string} è stata rimossa")
	public void removeBookCopy(String isbn) {
		givenCopyRemoved(isbn);
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

	@Then("la risposta contiene i seguenti campi:")
	public void checkResponseContent(Map<String, String> expectedRaw) {

		var context = JsonPath.parse(world.lastBody);

		Map<String, ExpectedValue> expectedContent = new LinkedHashMap<>();
		expectedRaw.forEach((k, v) -> {
			String resolved = Helper.resolve(v, world);
			expectedContent.put(k, Helper.normalizeExpected(resolved));
		});

		expectedContent.forEach((key, expectedValue) -> {
			Object actualValue = context.read(String.format("$.%s", key));
			Assertions.assertNotNull(actualValue, String.format("Missing field in response: %s", key));
			Assertions.assertTrue(expectedValue.matches(actualValue),
					String.format("Mismatch on field: %s, actual value: %s", key, actualValue));
		});

	}

	@Then("il libro {string} ha totalCopies = {int}, borrowedCopies = {int}, availableCopies = {int}, reservedCopies = {int}")
	public void checkBookView(String isbn, int totalCopies, int borrowedCopies, int availableCopies,
			int reservedCopies) {

		Optional<BookView> bookView = bookViewRepository.findById(isbn);

		if (bookView.isEmpty()) {
			throw new AssertionError(String.format("Book not found, ISBN: %s", isbn));
		}

		Assertions.assertEquals(totalCopies, bookView.get().totalCopies());
		Assertions.assertEquals(borrowedCopies, bookView.get().borrowedCopies());
		Assertions.assertEquals(availableCopies, bookView.get().availableCopies());
		Assertions.assertEquals(reservedCopies, bookView.get().reservedCopies());
	}

	/*
	 * ############################### THEN #####################################
	 */
	
	private void givenBookRegistered(String isbn, String author, String title, DocString description) {
		String isbnValue = isbn(isbn);
		BookAggregate book = aggregateFactory.loadBook(isbnValue);
		book.register(Book.create(isbnValue, author, title, description.getContent()));
	}

	private void givenCopiesAdded(int quantity, String isbn) {
		String isbnValue = isbn(isbn);
		BookAggregate book = aggregateFactory.loadBook(isbnValue);
		book.addCopies(quantity);
	}
	
	private void givenBookBorrowed(String isbn) {
		String loanId = UUID.randomUUID().toString();
		String userId = UUID.randomUUID().toString();
		String isbnValue = isbn(isbn);

		LoanAggregate loanAggregate = aggregateFactory.loadLoan(loanId);
		Loan loan = Loan.create(new LoanIdentifier(loanId), isbnValue, userId, LocalDate.now(), null);
		loanAggregate.add(loan);

		loanAggregate.requestConfirm();
	}
	
	private void givenCopyRemoved(String isbn) {
		String isbnValue = isbn(isbn);
		BookAggregate book = aggregateFactory.loadBook(isbnValue);
		book.removeCopies(1, "");
	}
	
	private String isbn(String isbn) {
		return ISBN.of(isbn).getValue();
	}
		
}
