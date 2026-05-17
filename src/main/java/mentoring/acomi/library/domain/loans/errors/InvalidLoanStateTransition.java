package mentoring.acomi.library.domain.loans.errors;

import mentoring.acomi.library.domain.common.errors.DomainError;

public class InvalidLoanStateTransition extends DomainError {

	private static final long serialVersionUID = 9103344839514959276L;
	
	private static final String code = "INVALID_STATE_TRANSATION";
	
	public InvalidLoanStateTransition(String message) {
		super(code, message);
	}

}
