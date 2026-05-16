package mentoring.acomi.library.domain.books.errors;

import mentoring.acomi.library.domain.common.errors.DomainError;

public class InvalidQuantity extends DomainError {

	private static final long serialVersionUID = 1L;
	
	private static final String code = "INVALID_BOOK_COPY_QUANTITY";
	
	public InvalidQuantity(String message) {
		super(code, message);
	}

}
