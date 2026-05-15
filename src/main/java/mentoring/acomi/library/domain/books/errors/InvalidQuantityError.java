package mentoring.acomi.library.domain.books.errors;

import mentoring.acomi.library.domain.common.errors.DomainError;

public class InvalidQuantityError extends DomainError {

	private static final long serialVersionUID = 1L;
	
	private static final String code = "INVALID_COPY_BOOK_QUANTITY";
	
	public InvalidQuantityError(String message) {
		super(code, message);
	}

}
