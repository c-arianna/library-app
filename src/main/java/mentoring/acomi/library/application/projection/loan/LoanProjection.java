package mentoring.acomi.library.application.projection.loan;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import mentoring.acomi.library.application.repositories.LoanViewRepository;
import mentoring.acomi.library.application.view.LoanView;
import mentoring.acomi.library.domain.common.DateRange;
import mentoring.acomi.library.domain.events.LoanCanceledEvent;
import mentoring.acomi.library.domain.events.LoanConfirmedEvent;
import mentoring.acomi.library.domain.events.LoanEvent;
import mentoring.acomi.library.domain.events.LoanFailedEvent;
import mentoring.acomi.library.domain.events.LoanRequestedEvent;
import mentoring.acomi.library.domain.events.LoanReservedEvent;
import mentoring.acomi.library.domain.events.payload.LoanRequestPayload;
import mentoring.acomi.library.domain.model.loans.LoanStatus;

@Component
public class LoanProjection {

	private final LoanViewRepository repository;

	public LoanProjection(LoanViewRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public void updateView(LoanEvent event) {

		switch (event) {
			case LoanRequestedEvent e -> repository.insertRequest(getLoan(e.payload()));
			case LoanFailedEvent e -> repository.updateStatus(e.payload().id(), LoanStatus.FAILED);
			case LoanReservedEvent e -> repository.updateStatus(e.payload().id(), LoanStatus.RESERVED);
			case LoanConfirmedEvent e -> repository.updateStatus(e.payload().id(), LoanStatus.CONFIRMED);
			case LoanCanceledEvent e -> repository.updateStatus(e.payload().id(), LoanStatus.CANCELED);
		}

	}

	private LoanView getLoan(LoanRequestPayload payload) {
		DateRange period = payload.period();
		return new LoanView(payload.id(), payload.isbn(), payload.userId(), period.getStart(), period.getEnd(), payload.status());
	}

}