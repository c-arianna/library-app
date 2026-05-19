package mentoring.acomi.library.application.aggregates;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import mentoring.acomi.library.application.LoanFailedReason;
import mentoring.acomi.library.domain.common.DateRange;
import mentoring.acomi.library.domain.events.LoanCanceledEvent;
import mentoring.acomi.library.domain.events.LoanConfirmedEvent;
import mentoring.acomi.library.domain.events.LoanEvent;
import mentoring.acomi.library.domain.events.LoanFailedEvent;
import mentoring.acomi.library.domain.events.LoanRequestedEvent;
import mentoring.acomi.library.domain.events.LoanReservedEvent;
import mentoring.acomi.library.domain.events.LoanReturnedEvent;
import mentoring.acomi.library.domain.events.payload.LoanFailedPayload;
import mentoring.acomi.library.domain.events.payload.LoanPayload;
import mentoring.acomi.library.domain.events.payload.LoanRequestPayload;
import mentoring.acomi.library.domain.loans.errors.InvalidLoanStateTransition;
import mentoring.acomi.library.domain.loans.errors.LoanNotExist;
import mentoring.acomi.library.domain.model.loans.Loan;
import mentoring.acomi.library.domain.model.loans.LoanIdentifier;
import mentoring.acomi.library.domain.model.loans.LoanStatus;

public class LoanAggregate extends AggregateRoot<LoanIdentifier, LoanEvent> {

	Map<LoanStatus, List<LoanStatus>> allowedTransitions = Map.of(
			LoanStatus.PENDING, List.of(LoanStatus.RESERVED, LoanStatus.FAILED), 
			LoanStatus.CANCELED, List.of(),
			LoanStatus.RETURNED, List.of(), 
			LoanStatus.FAILED, List.of(), 
			LoanStatus.CONFIRMED, List.of(LoanStatus.RETURNED), 
			LoanStatus.RESERVED, List.of(LoanStatus.CONFIRMED, LoanStatus.CANCELED, LoanStatus.FAILED));

	private boolean isCreated = false;
	private LoanStatus status;
	private String isbn;
	private String userId;
	private DateRange period;

	public LoanAggregate(LoanIdentifier id, Consumer<LoanEvent> dispatcher, List<LoanEvent> events) {
		super(id, dispatcher);
		this.replay(events);
	}

	@Override
	void apply(LoanEvent event) {
		switch (event) {
		case LoanRequestedEvent e -> applyLoanRequestedEvent(e);
		case LoanFailedEvent e -> applyLoanFailedEvent(e);
		case LoanReservedEvent e -> applyLoanReservedEvent(e);
		case LoanConfirmedEvent e -> applyLoanConfirmedEvent(e);
		case LoanCanceledEvent e -> applyLoanCanceledEvent(e);
		case LoanReturnedEvent e -> applyLoanReturnedEvent(e);
		}

	}

	private void applyLoanRequestedEvent(LoanRequestedEvent event) {
		LoanRequestPayload payload = event.payload();
		status = LoanStatus.PENDING;
		isbn = payload.isbn();
		userId = payload.userId();
		period = payload.period();
		isCreated = true;
	}

	private void applyLoanFailedEvent(LoanFailedEvent e) {
		status = LoanStatus.FAILED;
	}

	private void applyLoanReservedEvent(LoanReservedEvent e) {
		status = LoanStatus.RESERVED;
	}

	private void applyLoanConfirmedEvent(LoanConfirmedEvent e) {
		status = LoanStatus.CONFIRMED;
	}
	
	private void applyLoanCanceledEvent(LoanCanceledEvent e) {
		status = LoanStatus.CANCELED;
	}

	private void applyLoanReturnedEvent(LoanReturnedEvent e) {
		status = LoanStatus.RETURNED;
	}
	
	public void add(Loan loan) {

		if (this.isCreated) {
			throw new InvalidLoanStateTransition("Loan Already Created");
		}

		LoanRequestPayload payload = new LoanRequestPayload(loan.getId(), loan.getIsbn(), loan.getUserId(),
				loan.getPeriod(), LoanStatus.PENDING);
		LoanRequestedEvent event = new LoanRequestedEvent(loan.getId(), payload, Instant.now());
		manageEvent(event);
	}

	public void reserve() {

		ensureCreated();

		if (!LoanStatus.RESERVED.equals(status)) {

			ensureTransitionAllowed(LoanStatus.RESERVED);

			LoanReservedEvent event = new LoanReservedEvent(id.getValue(), new LoanPayload(id.getValue(), isbn, userId),
					Instant.now());
			manageEvent(event);
		}

	}

	public void fail(LoanFailedReason reason) {

		ensureCreated();

		if (!LoanStatus.CANCELED.equals(status) && !LoanStatus.FAILED.equals(status)
				&& !LoanStatus.RETURNED.equals(status)) {

			ensureTransitionAllowed(LoanStatus.FAILED);

			LoanFailedEvent event = new LoanFailedEvent(id.getValue(), new LoanFailedPayload(id.getValue(), reason),
					Instant.now());
			manageEvent(event);
		}

	}

	public void confirm() {

		ensureCreated();

		if (!LoanStatus.CONFIRMED.equals(status)) {

			ensureTransitionAllowed(LoanStatus.CONFIRMED);

			LoanConfirmedEvent event = new LoanConfirmedEvent(id.getValue(),
					new LoanPayload(id.getValue(), isbn, userId), Instant.now());
			manageEvent(event);
		}

	}

	public void cancel() {

		ensureCreated();

		if (!LoanStatus.CANCELED.equals(status)) {
			
			ensureTransitionAllowed(LoanStatus.CANCELED);
			
			LoanCanceledEvent event = new LoanCanceledEvent(id.getValue(),
					new LoanPayload(id.getValue(), isbn, userId), Instant.now());
			manageEvent(event);
		}

	}
	
	public void returnLoan() {
		
		ensureCreated();
		
		if (!LoanStatus.RETURNED.equals(status)) {
			
			ensureTransitionAllowed(LoanStatus.RETURNED);
			
			LoanReturnedEvent event = new LoanReturnedEvent(id.getValue(),
					new LoanPayload(id.getValue(), isbn, userId), Instant.now());
			manageEvent(event);
		}
		
	}
	
	public void ensureCreated() {
		if (!isCreated) {
			throw new LoanNotExist("Loan not exists");
		}
	}

	private void ensureTransitionAllowed(LoanStatus next) {

		List<LoanStatus> transitions = allowedTransitions.get(status);
		if (transitions.isEmpty() || !transitions.contains(next)) {
			throw new InvalidLoanStateTransition(
					String.format("Status transition from: %s, to: %s not allowed", status.name(), next.name()));
		}

	}

	public boolean isReservableState() {
		return LoanStatus.RESERVED.equals(status);
	}

	public String getIsbn() {
		return isbn;
	}

	public String getUserId() {
		return userId;
	}

}
