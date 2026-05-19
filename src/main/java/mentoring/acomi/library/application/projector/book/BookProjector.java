package mentoring.acomi.library.application.projector.book;

import org.springframework.stereotype.Component;

import mentoring.acomi.library.application.projection.book.BookProjection;
import mentoring.acomi.library.domain.events.BookStateEvent;

@Component
public class BookProjector {

	private final BookProjection projection;
	
	public BookProjector(BookProjection projection) {
		this.projection = projection;
	}
	
	public void project(BookStateEvent bookStateEvent) {
		projection.updateView(bookStateEvent);
	}
}
