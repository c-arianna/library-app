package mentoring.acomi.library.domain.loans.errors;

import mentoring.acomi.library.domain.common.errors.DomainError;

public class CannotBorrowWithoutReservation extends DomainError {

	private static final long serialVersionUID = 5697491593877360104L;

	private static final String code = "RESERVATION_NOT_FOUND";
	
	public CannotBorrowWithoutReservation(String message) {
		super(code, message);
	}

}
