package mentoring.acomi.library.application;

import org.springframework.context.annotation.Configuration;

import mentoring.acomi.library.application.eventhandler.EventDispatcher;
import mentoring.acomi.library.application.projector.book.BookProjector;

@Configuration
public class ApplicationEventConfiguration {

	public ApplicationEventConfiguration(EventDispatcher eventDispatcher, BookProjector bookProjector) {
		eventDispatcher.subscribe("BookRegistered", bookProjector::project);
	}

}
