package mentoring.acomi.library.application.eventhandler;

import java.util.function.Consumer;

import mentoring.acomi.library.domain.events.DomainEvent;
import mentoring.acomi.library.domain.events.DomainEventType;

public interface EventDispatcher {
	public void dispatch(DomainEvent event);
	public <E extends DomainEvent> void subscribe(DomainEventType eventType, Class<E> eventClass, Consumer<E> callback);

}
