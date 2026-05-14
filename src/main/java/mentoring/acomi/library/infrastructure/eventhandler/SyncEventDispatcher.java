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
import mentoring.acomi.library.domain.events.BookRegistered;

@Component
public class SyncEventDispatcher implements EventDispatcher {

	private Map<String, List<Consumer<BookRegistered>>> subscribers = new ConcurrentHashMap<>();

	private final Logger logger;

	public SyncEventDispatcher(@Value("${spring.application.name}") String applicationName) {
		this.logger = LogManager.getLogger(applicationName);
	}
	
	@Override
	public void dispatch(BookRegistered event) {
		
		List<Consumer<BookRegistered>> callbacks = subscribers.getOrDefault(event.getType(), List.of());
		
		for(Consumer<BookRegistered> callback : callbacks) {
			try {
				callback.accept(event);
			}catch(Exception e){
				logger.error("[EventDispatcher] Subscriber failed, eventType={}", event.getType(), e);
			}
		}
		
	}

	@Override
	public void subscribe(String eventType, Consumer<BookRegistered> callback) {
		subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(callback);	
	}

}
