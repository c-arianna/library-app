package mentoring.acomi.library.application.projector.loan;

import org.springframework.stereotype.Component;

import mentoring.acomi.library.application.projection.loan.LoanProjection;
import mentoring.acomi.library.domain.events.LoanEvent;

@Component
public class LoanProjector {
	
	private final LoanProjection projection;
	
	public LoanProjector(LoanProjection projection) {
		this.projection = projection;
	}

	public void project(LoanEvent bookEvent) {
		projection.updateView(bookEvent);
	}
}
