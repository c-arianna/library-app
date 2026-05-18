package mentoring.acomi.library.application;

import org.springframework.context.annotation.Configuration;

import mentoring.acomi.library.application.eventhandler.EventDispatcher;
import mentoring.acomi.library.application.projector.book.BookProjector;
import mentoring.acomi.library.application.projector.loan.LoanProjector;
import mentoring.acomi.library.domain.events.BookEvent;
import mentoring.acomi.library.domain.events.BookReservedEvent;
import mentoring.acomi.library.domain.events.DomainEventType;
import mentoring.acomi.library.domain.events.LoanCanceledEvent;
import mentoring.acomi.library.domain.events.LoanEvent;
import mentoring.acomi.library.domain.events.LoanRequestedEvent;

@Configuration
public class ApplicationEventConfiguration {

	public ApplicationEventConfiguration(EventDispatcher eventDispatcher, BookProjector bookProjector,
			LoanProjector loanProjector, LoanLifecycleSaga saga) {
		
		eventDispatcher.subscribe(DomainEventType.BookRegistered, BookEvent.class, bookProjector::project);
		eventDispatcher.subscribe(DomainEventType.BookCopiesAdded, BookEvent.class, bookProjector::project);
		eventDispatcher.subscribe(DomainEventType.BookCopiesRemoved, BookEvent.class, bookProjector::project);
		eventDispatcher.subscribe(DomainEventType.BookReserved, BookEvent.class, bookProjector::project);
		eventDispatcher.subscribe(DomainEventType.BookBorrowed, BookEvent.class, bookProjector::project);
		eventDispatcher.subscribe(DomainEventType.BookReleased, BookEvent.class, bookProjector::project);
		
		eventDispatcher.subscribe(DomainEventType.LoanRequested, LoanEvent.class, loanProjector::project);
		eventDispatcher.subscribe(DomainEventType.LoanFailed, LoanEvent.class, loanProjector::project);
		eventDispatcher.subscribe(DomainEventType.LoanConfirmed, LoanEvent.class, loanProjector::project);
		eventDispatcher.subscribe(DomainEventType.LoanCanceled, LoanEvent.class, loanProjector::project);
		
		eventDispatcher.subscribe(DomainEventType.BookReserved, BookReservedEvent.class, saga::onBookReserved);
		eventDispatcher.subscribe(DomainEventType.LoanRequested, LoanRequestedEvent.class, saga::onLoanRequested);
		eventDispatcher.subscribe(DomainEventType.LoanCanceled, LoanCanceledEvent.class, saga::onLoanCanceled);

	}

}
