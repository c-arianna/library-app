package mentoring.acomi.library.application.services;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mentoring.acomi.library.application.LoanFailedReason;
import mentoring.acomi.library.application.aggregates.AggregateFactory;
import mentoring.acomi.library.application.aggregates.AggregateType;
import mentoring.acomi.library.application.aggregates.BookAggregate;
import mentoring.acomi.library.application.aggregates.LoanAggregate;
import mentoring.acomi.library.application.repositories.BookViewRepository;
import mentoring.acomi.library.application.repositories.EventRepository;
import mentoring.acomi.library.application.repositories.UserViewRepository;
import mentoring.acomi.library.application.view.BookView;
import mentoring.acomi.library.application.view.UserView;
import mentoring.acomi.library.domain.common.errors.ApplicationConflict;
import mentoring.acomi.library.domain.loans.errors.BookNotAvailable;
import mentoring.acomi.library.domain.loans.errors.BookNotFound;
import mentoring.acomi.library.domain.loans.errors.CannotBorrowWithoutReservation;
import mentoring.acomi.library.domain.loans.errors.InvalidLoanStateTransition;
import mentoring.acomi.library.domain.loans.errors.UserNotFound;
import mentoring.acomi.library.domain.model.loans.Loan;
import mentoring.acomi.library.domain.model.loans.LoanIdentifier;
import mentoring.acomi.library.infrastructure.dto.loans.AddLoanRequest;
import mentoring.acomi.library.infrastructure.dto.loans.LoanResponse;

@Service
public class LoanService {

	private final EventRepository eventRepository;
	private final BookViewRepository bookViewRepository;
	private final UserViewRepository userViewRepository;
	private final AggregateFactory aggregateFactory;

	public LoanService(EventRepository eventRepository, BookViewRepository bookViewRepository,
			UserViewRepository userViewRepository, AggregateFactory aggregateFactory) {
		this.eventRepository = eventRepository;
		this.bookViewRepository = bookViewRepository;
		this.userViewRepository = userViewRepository;
		this.aggregateFactory = aggregateFactory;
	}

	@Transactional
	public LoanResponse addLoan(AddLoanRequest request) {

		String loanId = UUID.randomUUID().toString();

		validateLoanRequest(request, loanId);

		Loan loan = Loan.create(new LoanIdentifier(loanId), request.isbn(), request.userId(), request.startDate(),
				request.endDate());

		LoanAggregate aggregate = aggregateFactory.loadLoan(loanId);
		aggregate.add(loan);

		return new LoanResponse(loanId);
	}

	@Transactional
	public void confirmLoan(String loanId) {
		LoanAggregate loanAggregate = aggregateFactory.loadLoan(loanId);

		loanAggregate.ensureCreated();
		
		if (!loanAggregate.isReservableState()) {
			throw new InvalidLoanStateTransition("Cannot confirm loan");
		}

		String isbn = loanAggregate.getIsbn();
		String userId = loanAggregate.getUserId();

		try {

			BookAggregate bookAggregate = aggregateFactory.loadBook(isbn);
			bookAggregate.borrow(loanId, userId);

			loanAggregate.confirm();
			
		} catch (CannotBorrowWithoutReservation e) {
			loanAggregate.fail(LoanFailedReason.RESERVATION_MISSING);
			throw e;
		} catch (BookNotAvailable e) {
			loanAggregate.fail(LoanFailedReason.BOOK_NOT_AVAILABLE);
			throw e;
		}

	}
	
	@Transactional
	public void cancelLoan(String loanId) {
		LoanAggregate aggregate = aggregateFactory.loadLoan(loanId);
		aggregate.cancel();		
	}

	private void validateLoanRequest(AddLoanRequest request, String loanId) {

		if (eventRepository.exists(AggregateType.LOAN.name(), loanId)) {
			throw new ApplicationConflict("LOAN_ALREADY_EXISTS", String.format("Loan ID: %s", loanId));
		}

		String isbn = request.isbn();

		Optional<BookView> bookView = bookViewRepository.findById(isbn);

		if (bookView.isEmpty()) {
			throw new BookNotFound(String.format("Book not found, ISBN: %s", isbn));
		}

		int availableCopies = bookView.get().availableCopies();

		if (availableCopies <= 0) {
			throw new BookNotAvailable(String.format("There are no available copies for ISBN: %s", isbn));
		}

		String userId = request.userId();
		Optional<UserView> userView = userViewRepository.findById(userId);

		if (userView.isEmpty()) {
			throw new UserNotFound(String.format("User not found, id: %s", userId));
		}
	}
}
