package mentoring.acomi.library.application;

import org.springframework.context.annotation.Configuration;

import mentoring.acomi.library.application.eventhandler.EventDispatcher;
import mentoring.acomi.library.application.projector.book.BookProjector;
import mentoring.acomi.library.domain.events.DomainEventType;

@Configuration
public class ApplicationEventConfiguration {

	public ApplicationEventConfiguration(EventDispatcher eventDispatcher, BookProjector bookProjector) {
		eventDispatcher.subscribe(DomainEventType.BookRegistered, bookProjector::project);
		eventDispatcher.subscribe(DomainEventType.BookCopyAdded, bookProjector::project);
	}

}
