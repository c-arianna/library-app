package mentoring.acomi.library.domain.model.loans;

import org.springframework.util.ObjectUtils;

import mentoring.acomi.library.domain.common.Identifier;
import mentoring.acomi.library.domain.loans.errors.InvalidLoanId;

public class LoanIdentifier extends Identifier {

	public LoanIdentifier(String value) {
		super(LoanIdentifier.validate(value));
	}

	private static String validate(String value) {
		if (ObjectUtils.isEmpty(value)) {
			throw new InvalidLoanId("Loan ID is required");
		}
		
		return value;
	}

}
