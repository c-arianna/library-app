package mentoring.acomi.library.domain.loans.errors;

import mentoring.acomi.library.domain.common.errors.DomainError;

public class LoanNotExist extends DomainError{

	private static final long serialVersionUID = 6817599286997595676L;

	private static final String code = "LOAN_NOT_CREATED";
	
	public LoanNotExist(String message) {
		super(code, message);
	}

}
