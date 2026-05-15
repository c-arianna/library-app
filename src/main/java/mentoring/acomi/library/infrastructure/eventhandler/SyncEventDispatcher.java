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
import mentoring.acomi.library.domain.events.DomainEventType;
import mentoring.acomi.library.domain.events.books.BookEvent;

@Component
public class SyncEventDispatcher implements EventDispatcher {

	private Map<DomainEventType, List<Consumer<BookEvent>>> subscribers = new ConcurrentHashMap<>();

	private final Logger logger;

	public SyncEventDispatcher(@Value("${spring.application.name}") String applicationName) {
		this.logger = LogManager.getLogger(applicationName);
	}
	
	@Override
	public void dispatch(BookEvent event) {
		
		List<Consumer<BookEvent>> callbacks = subscribers.getOrDefault(event.type(), List.of());
		
		for(Consumer<BookEvent> callback : callbacks) {
			try {
				callback.accept(event);
			}catch(Exception e){
				logger.error("[EventDispatcher] Subscriber failed, eventType={}", event.type(), e);
			}
		}
		
	}

	@Override
	public void subscribe(DomainEventType eventType, Consumer<BookEvent> callback) {
		subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(callback);	
	}

}
