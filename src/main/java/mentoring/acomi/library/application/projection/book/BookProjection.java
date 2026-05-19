package mentoring.acomi.library.application.projection.book;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import mentoring.acomi.library.application.repositories.BookViewRepository;
import mentoring.acomi.library.application.view.BookView;
import mentoring.acomi.library.domain.events.BookBorrowedEvent;
import mentoring.acomi.library.domain.events.BookCopiesAddedEvent;
import mentoring.acomi.library.domain.events.BookCopiesRemovedEvent;
import mentoring.acomi.library.domain.events.BookEvent;
import mentoring.acomi.library.domain.events.BookRegisteredEvent;
import mentoring.acomi.library.domain.events.BookReleasedEvent;
import mentoring.acomi.library.domain.events.BookReservedEvent;
import mentoring.acomi.library.domain.events.BookReturnedEvent;
import mentoring.acomi.library.domain.events.payload.BookRegisteredPayload;

@Component
public class BookProjection {

	private final BookViewRepository repository;

	public BookProjection(BookViewRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public void updateView(BookEvent event) {

		switch (event) {
			case BookRegisteredEvent e -> repository.addBook(getBook(e.payload()));
			case BookCopiesAddedEvent e -> repository.addCopies(e.payload().isbn(), e.payload().quantity());
			case BookCopiesRemovedEvent e -> repository.removeCopies(e.payload().isbn(), e.payload().quantity());
			case BookReservedEvent e -> repository.reserve(e.payload().isbn());
			case BookBorrowedEvent e -> repository.borrow(e.payload().isbn());
			case BookReleasedEvent e -> repository.release(e.payload().isbn());
			case BookReturnedEvent e -> repository.returnBorrowed(e.payload().isbn());
		}

	}

	private BookView getBook(BookRegisteredPayload payload) {
		return new BookView(payload.isbn(), payload.author(), payload.title(), payload.description(), 0, 0, 0, 0 );
	}
}