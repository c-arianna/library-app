package mentoring.acomi.library.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import mentoring.acomi.library.application.aggregates.AggregateFactory;
import mentoring.acomi.library.application.aggregates.BookAggregate;
import mentoring.acomi.library.application.aggregates.LoanAggregate;
import mentoring.acomi.library.domain.events.BookBorrowRejectedEvent;
import mentoring.acomi.library.domain.events.BookBorrowedEvent;
import mentoring.acomi.library.domain.events.BookReservationRejectedEvent;
import mentoring.acomi.library.domain.events.BookReservedEvent;
import mentoring.acomi.library.domain.events.LoanCanceledEvent;
import mentoring.acomi.library.domain.events.LoanConfirmRequestedEvent;
import mentoring.acomi.library.domain.events.LoanRequestedEvent;
import mentoring.acomi.library.domain.events.LoanReturnedEvent;

@Component
public class LoanLifecycleSaga {

	private final AggregateFactory aggregateFactory;
	private final Logger logger;

	public LoanLifecycleSaga(AggregateFactory aggregateFactory,
			@Value("${spring.application.name}") String applicationName) {
		this.aggregateFactory = aggregateFactory;
		this.logger = LogManager.getLogger(applicationName);
	}

	public void onBookReserved(BookReservedEvent event) {
		handleBookReserved(event);
	}

	public void onBookReservationRejected(BookReservationRejectedEvent event) {
		handleBookReservationRejected(event);
	}

	public void onBookBorrowed(BookBorrowedEvent event) {
		handleBookBorrowed(event);
	}

	public void onBookBorrowRejected(BookBorrowRejectedEvent event) {
		handleBookBorrowRejected(event);
	}

	public void onLoanRequested(LoanRequestedEvent event) {
		handleLoanRequested(event);
	}

	public void onLoanConfirmRequested(LoanConfirmRequestedEvent event) {
		handleLoanConfirmRequested(event);
	}

	public void onLoanCanceled(LoanCanceledEvent event) {
		handleLoanCanceled(event);
	}

	public void onLoanReturned(LoanReturnedEvent event) {
		handleLoanReturned(event);
	}

	private void handleLoanRequested(LoanRequestedEvent event) {

		var payload = event.payload();
		BookAggregate book = aggregateFactory.loadBook(payload.isbn());
		book.reserve(payload.id(), payload.userId());
	}

	private void handleBookReserved(BookReservedEvent event) {
		var payload = event.payload();
		String loanId = payload.loanId();

		LoanAggregate loan = aggregateFactory.loadLoan(loanId);
		loan.reserve();
	}

	private void handleBookReservationRejected(BookReservationRejectedEvent event) {
		var loanId = event.payload().loanId();
		var reason = event.payload().reason();

		LoanAggregate loan = aggregateFactory.loadLoan(loanId);

		if (reason == BookReservationRejectReason.BOOK_NOT_AVAILABLE) {
			loan.fail(LoanFailedReason.BOOK_NOT_AVAILABLE);
		} else {
			loan.fail(LoanFailedReason.BOOK_NOT_FOUND);
		}
	}

	private void handleBookBorrowed(BookBorrowedEvent event) {
		var loanId = event.payload().loanId();

		LoanAggregate loan = aggregateFactory.loadLoan(loanId);
		loan.confirm();
	}

	private void handleBookBorrowRejected(BookBorrowRejectedEvent event) {
		var loanId = event.payload().loanId();
		var reason = event.payload().reason();

		LoanAggregate loan = aggregateFactory.loadLoan(loanId);

		if (reason == BookBorrowRejectReason.RESERVATION_MISSING) {
			loan.fail(LoanFailedReason.RESERVATION_MISSING);
		} else {
			loan.fail(LoanFailedReason.BOOK_NOT_AVAILABLE);
		}
	}
	
	private void handleLoanConfirmRequested(LoanConfirmRequestedEvent event) {
		var p = event.payload();

		BookAggregate book = aggregateFactory.loadBook(p.isbn());
		book.borrow(p.id(), p.userId());
	}

	private void handleLoanCanceled(LoanCanceledEvent event) {

		var payload = event.payload();
		String isbn = payload.isbn();
		String loanId = payload.id();
		String userId = payload.userId();

		try {
			BookAggregate book = aggregateFactory.loadBook(isbn);
			book.release(loanId, userId);

		} catch (Exception e) {
			logger.warn("Ignoring error on LoanCanceled, loanId={}", loanId, e);
		}
	}

	private void handleLoanReturned(LoanReturnedEvent event) {

		var payload = event.payload();
		String isbn = payload.isbn();
		String loanId = payload.id();
		String userId = payload.userId();

		try {
			BookAggregate book = aggregateFactory.loadBook(isbn);
			book.returnBorrowed(loanId, userId);

		} catch (Exception e) {
			logger.warn("Ignoring error on LoanReturned, loanId={}", loanId, e);
		}
	}

}