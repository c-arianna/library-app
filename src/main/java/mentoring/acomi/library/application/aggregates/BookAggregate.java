package mentoring.acomi.library.application.aggregates;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import mentoring.acomi.library.domain.books.errors.BookNotRegistered;
import mentoring.acomi.library.domain.books.errors.CannotRemoveBookCopies;
import mentoring.acomi.library.domain.books.errors.InvalidIsbn;
import mentoring.acomi.library.domain.books.errors.InvalidQuantity;
import mentoring.acomi.library.domain.events.books.BookCopiesAddedEvent;
import mentoring.acomi.library.domain.events.books.BookCopiesAddedPayload;
import mentoring.acomi.library.domain.events.books.BookCopiesRemovedEvent;
import mentoring.acomi.library.domain.events.books.BookCopiesRemovedPayload;
import mentoring.acomi.library.domain.events.books.BookEvent;
import mentoring.acomi.library.domain.events.books.BookRegisteredEvent;
import mentoring.acomi.library.domain.events.books.BookRegisteredPayload;
import mentoring.acomi.library.domain.model.books.Book;
import mentoring.acomi.library.domain.model.books.ISBN;

public class BookAggregate {

	public static final String aggregateType = "Book";

	private boolean isRegistered = false;
	private int totalCopies = 0;
	private int borrowed = 0;
	private int reserved = 0;
	
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
			case BookCopiesAddedEvent e -> applyBookCopiesAdded(e);
			case BookCopiesRemovedEvent e -> applyBookCopiesRemoved(e);
		}
	}

	private void applyBookEventRegistered(BookRegisteredEvent event) {
		isRegistered = true;
	}

	private void applyBookCopiesAdded(BookCopiesAddedEvent event) {
		totalCopies += event.payload().quantity();
	}

	private void applyBookCopiesRemoved(BookCopiesRemovedEvent event) {
		totalCopies -= event.payload().quantity();
	}
	
	public void register(Book book) {

		if (!book.getIsbn().equals(isbn.getValue())) {
			throw new InvalidIsbn("Book ISBN does not match aggregate id");
		}

		if (!isRegistered) {
			BookRegisteredPayload payload = new BookRegisteredPayload(book.getIsbn(), book.getAuthor(), book.getTitle(),
					book.getDescription());
			BookRegisteredEvent event = new BookRegisteredEvent(BookAggregate.aggregateType, book.getIsbn(), payload,
					Instant.now());
			manageEvent(event);
		}

	}

	public void addCopies(int quantity) {

		ensureRegistered();

		if (quantity <= 0) {
			throw new InvalidQuantity("quantity must be > 0");
		}

		BookCopiesAddedEvent event = new BookCopiesAddedEvent(BookAggregate.aggregateType, isbn.getValue(),
				new BookCopiesAddedPayload(isbn.getValue(), quantity), Instant.now());
		manageEvent(event);

	}

	public void removeCopies(int quantity, String reason) {
		
		ensureRegistered();
		
		if (quantity <= 0) {
			throw new InvalidQuantity("quantity must be > 0");
		}
		
		int copiesAvailable = totalCopies - (borrowed + reserved);
		if (copiesAvailable < quantity) {
		      throw new CannotRemoveBookCopies(String.format("Cannot remove %d copies, total copies available %d", 
		    		  quantity, copiesAvailable));
		}
		
		BookCopiesRemovedEvent event = new BookCopiesRemovedEvent(BookAggregate.aggregateType, isbn.getValue(),
				new BookCopiesRemovedPayload(isbn.getValue(), quantity, reason), Instant.now());
		manageEvent(event);
		
	}

	private void manageEvent(BookEvent event) {
		apply(event);
		dispatcher.accept(event);
	}

	private void ensureRegistered() {
		if (!isRegistered) {
			throw new BookNotRegistered(String.format("Book not registered, ISBN: %s", isbn));
		}

	}

}
