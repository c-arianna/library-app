package mentoring.acomi.library.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import mentoring.acomi.library.application.aggregates.AggregateFactory;
import mentoring.acomi.library.application.aggregates.BookAggregate;
import mentoring.acomi.library.application.aggregates.LoanAggregate;
import mentoring.acomi.library.domain.books.errors.BookNotRegistered;
import mentoring.acomi.library.domain.events.BookReservedEvent;
import mentoring.acomi.library.domain.events.LoanCanceledEvent;
import mentoring.acomi.library.domain.events.LoanRequestedEvent;
import mentoring.acomi.library.domain.loans.errors.BookNotAvailable;
import mentoring.acomi.library.domain.loans.errors.BookNotFound;

@Component
public class LoanLifecycleSaga {

	private final AggregateFactory aggregateFactory;
	private final Logger logger;

	public LoanLifecycleSaga(AggregateFactory aggregateFactory, @Value("${spring.application.name}") String applicationName) {
		this.aggregateFactory = aggregateFactory;
		this.logger = LogManager.getLogger(applicationName);
	}

	public void onLoanRequested(LoanRequestedEvent event) {
		handleLoanRequested(event);
	}

	public void onBookReserved(BookReservedEvent event) {
		handleBookReserved(event);
	}

	public void onLoanCanceled(LoanCanceledEvent event) {
		handleLoanCanceled(event);
	}

	private void handleLoanRequested(LoanRequestedEvent event) {

		var payload = event.payload();
		String isbn = payload.isbn();
		String loanId = payload.id();
		String userId = payload.userId();

		try {
			BookAggregate book = aggregateFactory.loadBook(isbn);
			book.reserve(loanId, userId);

		} catch (BookNotAvailable e) {
			fail(loanId, LoanFailedReason.BOOK_NOT_AVAILABLE);

		} catch (BookNotRegistered | BookNotFound e) {
			fail(loanId, LoanFailedReason.BOOK_NOT_FOUND);

		} catch (Exception e) {
			throw e;
		}
	}

	private void handleBookReserved(BookReservedEvent event) {
		var payload = event.payload();
		String loanId = payload.loanId();

		LoanAggregate loan = aggregateFactory.loadLoan(loanId);
		loan.reserve();
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

	private void fail(String loanId, LoanFailedReason reason) {
		LoanAggregate loan = aggregateFactory.loadLoan(loanId);
		loan.fail(reason);
	}
}