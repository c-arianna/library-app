package mentoring.acomi.library.application.projection.book;

import org.springframework.stereotype.Component;

import mentoring.acomi.library.application.repositories.BookViewRepository;
import mentoring.acomi.library.domain.events.BookRegistered;

@Component
public class BookProjection {
	
	private final BookViewRepository repository;
	
	public BookProjection(BookViewRepository repository) {
		this.repository = repository;
	}

	public void updateView(BookRegistered bookRegistered) {
		repository.addBook(bookRegistered.getPayload());
	}
}