package mentoring.acomi.library.application;
import mentoring.acomi.library.infrastructure.persistence.mapper.EventJpaMapper;
import org.springframework.context.annotation.Configuration;

import mentoring.acomi.library.application.eventhandler.EventDispatcher;
import mentoring.acomi.library.application.projector.book.BookProjector;
import mentoring.acomi.library.application.projector.loan.LoanProjector;
import mentoring.acomi.library.domain.events.BookBorrowRejectedEvent;
import mentoring.acomi.library.domain.events.BookBorrowedEvent;
import mentoring.acomi.library.domain.events.BookReservationRejectedEvent;
import mentoring.acomi.library.domain.events.BookReservedEvent;
import mentoring.acomi.library.domain.events.BookStateEvent;
import mentoring.acomi.library.domain.events.DomainEventType;
import mentoring.acomi.library.domain.events.LoanCanceledEvent;
import mentoring.acomi.library.domain.events.LoanConfirmRequestedEvent;
import mentoring.acomi.library.domain.events.LoanRequestedEvent;
import mentoring.acomi.library.domain.events.LoanReturnedEvent;
import mentoring.acomi.library.domain.events.LoanStateEvent;

@Configuration
public class ApplicationEventConfiguration {

    public ApplicationEventConfiguration(EventDispatcher eventDispatcher, BookProjector bookProjector,
			LoanProjector loanProjector, LoanLifecycleSaga saga, EventJpaMapper eventJpaMapper) {
		
		eventDispatcher.subscribe(DomainEventType.BookRegistered, BookStateEvent.class, bookProjector::project);
		eventDispatcher.subscribe(DomainEventType.BookCopiesAdded, BookStateEvent.class, bookProjector::project);
		eventDispatcher.subscribe(DomainEventType.BookCopiesRemoved, BookStateEvent.class, bookProjector::project);
		eventDispatcher.subscribe(DomainEventType.BookReserved, BookStateEvent.class, bookProjector::project);
		eventDispatcher.subscribe(DomainEventType.BookBorrowed, BookStateEvent.class, bookProjector::project);
		eventDispatcher.subscribe(DomainEventType.BookReleased, BookStateEvent.class, bookProjector::project);
		eventDispatcher.subscribe(DomainEventType.BookReturned, BookStateEvent.class, bookProjector::project);
		
		eventDispatcher.subscribe(DomainEventType.LoanRequested, LoanStateEvent.class, loanProjector::project);
		eventDispatcher.subscribe(DomainEventType.LoanFailed, LoanStateEvent.class, loanProjector::project);
		eventDispatcher.subscribe(DomainEventType.LoanConfirmed, LoanStateEvent.class, loanProjector::project);
		eventDispatcher.subscribe(DomainEventType.LoanCanceled, LoanStateEvent.class, loanProjector::project);
		eventDispatcher.subscribe(DomainEventType.LoanReserved, LoanStateEvent.class, loanProjector::project);
		eventDispatcher.subscribe(DomainEventType.LoanReturned, LoanStateEvent.class, loanProjector::project);
		
		eventDispatcher.subscribe(DomainEventType.BookReserved, BookReservedEvent.class, saga::onBookReserved);
		eventDispatcher.subscribe(DomainEventType.BookReservationRejected, BookReservationRejectedEvent.class, saga::onBookReservationRejected);
		eventDispatcher.subscribe(DomainEventType.BookBorrowed, BookBorrowedEvent.class, saga::onBookBorrowed);
		eventDispatcher.subscribe(DomainEventType.BookBorrowRejected, BookBorrowRejectedEvent.class, saga::onBookBorrowRejected);
				
		eventDispatcher.subscribe(DomainEventType.LoanRequested, LoanRequestedEvent.class, saga::onLoanRequested);
		eventDispatcher.subscribe(DomainEventType.LoanConfirmRequested, LoanConfirmRequestedEvent.class, saga::onLoanConfirmRequested);
		eventDispatcher.subscribe(DomainEventType.LoanCanceled, LoanCanceledEvent.class, saga::onLoanCanceled);
		eventDispatcher.subscribe(DomainEventType.LoanReturned, LoanReturnedEvent.class, saga::onLoanReturned);

	}

}
