package mentoring.acomi.library.application.aggregates;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import mentoring.acomi.library.domain.books.errors.BookNotRegistered;
import mentoring.acomi.library.domain.books.errors.CannotRemoveBookCopies;
import mentoring.acomi.library.domain.books.errors.InvalidIsbn;
import mentoring.acomi.library.domain.books.errors.InvalidQuantity;
import mentoring.acomi.library.domain.events.BookCopiesAddedEvent;
import mentoring.acomi.library.domain.events.BookCopiesRemovedEvent;
import mentoring.acomi.library.domain.events.BookEvent;
import mentoring.acomi.library.domain.events.BookRegisteredEvent;
import mentoring.acomi.library.domain.events.BookReservedEvent;
import mentoring.acomi.library.domain.events.payload.BookCopiesAddedPayload;
import mentoring.acomi.library.domain.events.payload.BookCopiesRemovedPayload;
import mentoring.acomi.library.domain.events.payload.BookLoanPayload;
import mentoring.acomi.library.domain.events.payload.BookRegisteredPayload;
import mentoring.acomi.library.domain.loans.errors.BookNotAvailable;
import mentoring.acomi.library.domain.model.books.Book;
import mentoring.acomi.library.domain.model.books.ISBN;

public class BookAggregate extends AggregateRoot<ISBN, BookEvent> {

	private boolean isRegistered = false;
	private int totalCopies = 0;
	private int borrowed = 0;
	private int reserved = 0;

	private Set<String> reservedLoans = new HashSet<>();
	private Set<String> borrowedLoans = new HashSet<>();

	public BookAggregate(ISBN id, Consumer<BookEvent> dispatcher, List<BookEvent> events) {
		super(id, dispatcher);
		this.replay(events);
	}

	@Override
	public void apply(BookEvent event) {
		switch (event) {
		case BookRegisteredEvent e -> applyBookEventRegistered(e);
		case BookCopiesAddedEvent e -> applyBookCopiesAdded(e);
		case BookCopiesRemovedEvent e -> applyBookCopiesRemoved(e);
		case BookReservedEvent e -> applyBookReserved(e);
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

	private void applyBookReserved(BookReservedEvent event) {

		String loanId = event.payload().loanId();
		if (!reservedLoans.contains(loanId) && !borrowedLoans.contains(loanId)) {
			reservedLoans.add(loanId);
			reserved += 1;
		}
	}

	public void register(Book book) {

		if (!book.getIsbn().equals(id.getValue())) {
			throw new InvalidIsbn("Book ISBN does not match aggregate id");
		}

		if (!isRegistered) {
			BookRegisteredPayload payload = new BookRegisteredPayload(book.getIsbn(), book.getAuthor(), book.getTitle(),
					book.getDescription());
			BookRegisteredEvent event = new BookRegisteredEvent(book.getIsbn(), payload, Instant.now());
			manageEvent(event);
		}

	}

	public void addCopies(int quantity) {

		ensureRegistered();

		if (quantity <= 0) {
			throw new InvalidQuantity("quantity must be > 0");
		}

		BookCopiesAddedEvent event = new BookCopiesAddedEvent(id.getValue(),
				new BookCopiesAddedPayload(id.getValue(), quantity), Instant.now());
		manageEvent(event);

	}

	public void removeCopies(int quantity, String reason) {

		ensureRegistered();

		if (quantity <= 0) {
			throw new InvalidQuantity("quantity must be > 0");
		}

		int copiesAvailable = totalCopies - (borrowed + reserved);
		if (copiesAvailable < quantity) {
			throw new CannotRemoveBookCopies(
					String.format("Cannot remove %d copies, total copies available %d", quantity, copiesAvailable));
		}

		BookCopiesRemovedEvent event = new BookCopiesRemovedEvent(id.getValue(),
				new BookCopiesRemovedPayload(id.getValue(), quantity, reason), Instant.now());
		manageEvent(event);

	}

	public void reserve(String loanId, String userId) {

		ensureRegistered();

		if (availableCopies() <= 0) {
			throw new BookNotAvailable(String.format("There are no available copies for ISBN: %s", id));
		}

		if (!reservedLoans.contains(loanId) && !borrowedLoans.contains(loanId)) {
			BookReservedEvent event = new BookReservedEvent(id.getValue(),
					new BookLoanPayload(id.getValue(), loanId, userId), Instant.now());
			manageEvent(event);
		}
	}

	private void ensureRegistered() {
		if (!isRegistered) {
			throw new BookNotRegistered(String.format("Book not registered, ISBN: %s", id));
		}

	}

	private int availableCopies() {
		return totalCopies - reserved - borrowed;
	}

}
