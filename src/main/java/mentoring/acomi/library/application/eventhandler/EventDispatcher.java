package mentoring.acomi.library.application.eventhandler;

import java.util.function.Consumer;

import mentoring.acomi.library.domain.events.DomainEventType;
import mentoring.acomi.library.domain.events.books.BookEvent;

public interface EventDispatcher {
	public void dispatch(BookEvent event);
	public void subscribe(DomainEventType eventType, Consumer<BookEvent> callback);
}
