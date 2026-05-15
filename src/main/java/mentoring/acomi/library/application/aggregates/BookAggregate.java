package mentoring.acomi.library.application.aggregates;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import mentoring.acomi.library.domain.books.errors.InvalidIsbn;
import mentoring.acomi.library.domain.events.BookRegistered;
import mentoring.acomi.library.domain.model.books.Book;
import mentoring.acomi.library.domain.model.books.ISBN;

public class BookAggregate {

	public static final String aggregateType = "Book";

	private boolean isRegistered = false;

	private ISBN isbn;
	private Consumer<BookRegistered> dispatcher;

	public BookAggregate(ISBN isbn, Consumer<BookRegistered> dispatcher, List<BookRegistered> events) {
		this.isbn = isbn;
		this.dispatcher = dispatcher;
		this.replay(events);
	}

	public void replay(List<BookRegistered> events) {
		for (BookRegistered event : events) {
			apply(event);
		}
	}

	private void apply(BookRegistered event) {
		isRegistered = true;
	}

	public void register(Book book) throws InvalidIsbn {

		if (!book.getIsbn().equals(isbn.getValue())) {
			throw new InvalidIsbn("Book ISBN does not match aggregate id");
		}

		if (!isRegistered) {
			BookRegistered event = new BookRegistered(BookAggregate.aggregateType, book.getIsbn(),
					book, Instant.now());
			apply(event);
			dispatcher.accept(event);
		}

	}

}
