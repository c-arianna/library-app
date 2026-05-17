package mentoring.acomi.library.application.aggregates;

import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import mentoring.acomi.library.application.eventhandler.EventDispatcher;
import mentoring.acomi.library.application.repositories.EventRepository;
import mentoring.acomi.library.domain.events.BookEvent;
import mentoring.acomi.library.domain.events.DomainEvent;
import mentoring.acomi.library.domain.events.LoanEvent;
import mentoring.acomi.library.domain.model.books.ISBN;
import mentoring.acomi.library.domain.model.loans.LoanIdentifier;

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

	public BookAggregate loadBook(String isbn) {

		List<BookEvent> events = eventRepository.loadStream(AggregateType.BOOK.name(), isbn, BookEvent.class);
		Consumer<BookEvent> dispatch = createDispatcher();

		return new BookAggregate(ISBN.of(isbn), dispatch, events);
	}

	public LoanAggregate loadLoan(String loanId) {
		List<LoanEvent> events = eventRepository.loadStream(AggregateType.LOAN.name(), loanId, LoanEvent.class);
		Consumer<LoanEvent> dispatch = createDispatcher();
		return new LoanAggregate(new LoanIdentifier(loanId), dispatch, events);
	}

	private <E extends DomainEvent> Consumer<E> createDispatcher() {
		return event -> {
			eventRepository.appendToStream(event);
			try {
				eventDispatcher.dispatch(event);
			} catch (Exception e) {
				logger.error("[Dispatch] error after event persistence, eventType={}", event.type(), e);
			}
		};
	}

}
