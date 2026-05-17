package mentoring.acomi.library.application;

import org.springframework.stereotype.Component;

import mentoring.acomi.library.application.aggregates.AggregateFactory;
import mentoring.acomi.library.application.aggregates.BookAggregate;
import mentoring.acomi.library.application.aggregates.LoanAggregate;
import mentoring.acomi.library.domain.books.errors.BookNotRegistered;
import mentoring.acomi.library.domain.events.LoanRequestedEvent;
import mentoring.acomi.library.domain.loans.errors.BookNotAvailable;
import mentoring.acomi.library.domain.loans.errors.BookNotFound;

@Component
public class LoanLifecycleSaga {

    private final AggregateFactory aggregateFactory;
    
    public LoanLifecycleSaga(AggregateFactory aggregateFactory) {
        this.aggregateFactory = aggregateFactory;
    }

    public void onLoanRequested(LoanRequestedEvent event) {
        handleLoanRequested(event);
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

    private void fail(String loanId, LoanFailedReason reason) {
        LoanAggregate loan = aggregateFactory.loadLoan(loanId);
        loan.fail(reason);
    }
}