package mentoring.acomi.library.application.aggregates;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import mentoring.acomi.library.domain.books.errors.BookNotRegisteredError;
import mentoring.acomi.library.domain.books.errors.InvalidIsbn;
import mentoring.acomi.library.domain.books.errors.InvalidQuantityError;
import mentoring.acomi.library.domain.events.books.BookCopyAddedEvent;
import mentoring.acomi.library.domain.events.books.BookCopyAddedPayload;
import mentoring.acomi.library.domain.events.books.BookEvent;
import mentoring.acomi.library.domain.events.books.BookRegisteredEvent;
import mentoring.acomi.library.domain.events.books.BookRegisteredPayload;
import mentoring.acomi.library.domain.model.books.Book;
import mentoring.acomi.library.domain.model.books.ISBN;

public class BookAggregate {

	public static final String aggregateType = "Book";

	private boolean isRegistered = false;
	private int totalCopies = 0;

	private ISBN isbn;
	private Consumer<BookEvent> dispatcher;

	public BookAggregate(ISBN isbn, Consumer<BookEvent> dispatcher, List<BookEvent> events) {
		this.isbn = isbn;
		this.dispatcher = dispatcher;
		this.replay(events);
	}

	public void replay(List<BookEvent> events) {
		for (BookEvent event : events) {
			apply(event);
		}
	}

	private void apply(BookEvent event) {
		switch (event) {
		case BookRegisteredEvent e -> applyBookEventRegistered(e);
		case BookCopyAddedEvent e -> applyBookCopyAddedEvent(e);
		}

	}

	private void applyBookEventRegistered(BookRegisteredEvent event) {
		isRegistered = true;
	}

	private void applyBookCopyAddedEvent(BookCopyAddedEvent event) {
		totalCopies += event.payload().getQuantity();
	}

	public void register(Book book) {

		if (!book.getIsbn().equals(isbn.getValue())) {
			throw new InvalidIsbn("Book ISBN does not match aggregate id");
		}

		if (!isRegistered) {
			BookRegisteredPayload payload = new BookRegisteredPayload(book.getIsbn(), book.getAuthor(),
					book.getTitle(), book.getDescription());
			BookRegisteredEvent event = new BookRegisteredEvent(BookAggregate.aggregateType, book.getIsbn(), 
					payload, Instant.now());
			manageEvent(event);
		}

	}

	public void addCopy(int quantity) {

		ensureRegistered();

		if (quantity <= 0) {
			throw new InvalidQuantityError("quantity must be > 0");
		}

		BookCopyAddedEvent event = new BookCopyAddedEvent(BookAggregate.aggregateType, isbn.getValue(),
				new BookCopyAddedPayload(isbn.getValue(), quantity), Instant.now());
		manageEvent(event);

	}

	private void manageEvent(BookEvent event) {
		apply(event);
		dispatcher.accept(event);
	}

	private void ensureRegistered() {
		if (!isRegistered) {
			throw new BookNotRegisteredError(String.format("Book not registered, ISBN: %s", isbn));
		}

	}

}
