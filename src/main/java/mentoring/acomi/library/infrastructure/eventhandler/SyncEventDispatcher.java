package mentoring.acomi.library.infrastructure.eventhandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import mentoring.acomi.library.application.eventhandler.EventDispatcher;
import mentoring.acomi.library.domain.events.DomainEvent;
import mentoring.acomi.library.domain.events.DomainEventType;

@Component
public class SyncEventDispatcher implements EventDispatcher {

	private Map<DomainEventType, List<Consumer<DomainEvent>>> subscribers = new ConcurrentHashMap<>();

	private final Logger logger;

	public SyncEventDispatcher(@Value("${spring.application.name}") String applicationName) {
		this.logger = LogManager.getLogger(applicationName);
	}

	@Override
	public void dispatch(DomainEvent event) {

		List<Consumer<DomainEvent>> callbacks = subscribers.getOrDefault(event.type(), List.of());

		for (Consumer<DomainEvent> callback : callbacks) {
			try {
				callback.accept(event);
			} catch (Exception e) {
				logger.error("[EventDispatcher] Subscriber failed, eventType={}", event.type(), e);
			}
		}

	}

	@Override
	public <E extends DomainEvent> void subscribe(DomainEventType eventType, Class<E> eventClass,
			Consumer<E> callback) {

		Consumer<DomainEvent> wrapper = ev -> {
			if (!eventClass.isInstance(ev)) {
				throw new IllegalStateException(String.format("Subscriber expected %s but got %s", 
						eventClass.getSimpleName(), ev.getClass().getSimpleName()));
			}
			callback.accept(eventClass.cast(ev));
		};

		subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(wrapper);
	}

}
