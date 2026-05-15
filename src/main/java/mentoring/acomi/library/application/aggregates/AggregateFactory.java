package mentoring.acomi.library.application.aggregates;

import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import mentoring.acomi.library.application.eventhandler.EventDispatcher;
import mentoring.acomi.library.application.repositories.EventRepository;
import mentoring.acomi.library.domain.events.books.BookEvent;
import mentoring.acomi.library.domain.model.books.ISBN;

@Service
public class AggregateFactory {

	private final EventRepository eventRepository;
	private final EventDispatcher eventDispatcher;
	private final Logger logger;
	
	public AggregateFactory(EventRepository eventRepository, EventDispatcher eventDispatcher, 
			@Value("${spring.application.name}") String applicationName) {
		this.eventRepository = eventRepository;
		this.eventDispatcher = eventDispatcher;
		this.logger = LogManager.getLogger(applicationName);
	}

	public BookAggregate load(String aggregateType, String aggregateId)  {

		Consumer<BookEvent> dispatch = event -> {
			eventRepository.appendToStream(event);
			try {
				eventDispatcher.dispatch(event);
			} catch (Exception e) {
				logger.error("[Dispatch] error after event persistence, eventType={}", e.getMessage(), e);
			}
		};

		List<BookEvent> events = eventRepository.loadStream(aggregateType, aggregateId);

		return new BookAggregate(ISBN.of(aggregateId), dispatch, events);
	
	}

}
