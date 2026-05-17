package mentoring.acomi.library.domain.loans.errors;

import mentoring.acomi.library.domain.common.errors.DomainError;

public class BookNotAvailable extends DomainError {

	private static final long serialVersionUID = -3979413694152467932L;

	private static final String code = "BOOK_NOT_AVAILABLE";
	
	public BookNotAvailable(String message) {
		super(code, message);
	}

}
