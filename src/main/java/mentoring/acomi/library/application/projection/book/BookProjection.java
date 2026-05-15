package mentoring.acomi.library.application.projection.book;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import mentoring.acomi.library.application.repositories.BookViewRepository;
import mentoring.acomi.library.domain.events.books.BookCopyAddedEvent;
import mentoring.acomi.library.domain.events.books.BookEvent;
import mentoring.acomi.library.domain.events.books.BookRegisteredEvent;
import mentoring.acomi.library.domain.events.books.BookRegisteredPayload;
import mentoring.acomi.library.domain.model.books.Book;

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
			case BookCopyAddedEvent e -> repository.addCopies(e.payload().getIsbn(), e.payload().getQuantity());
		}

	}

	private Book getBook(BookRegisteredPayload payload) {
		return Book.create(payload.isbn(), payload.author(), payload.title(), payload.description());
	}
}