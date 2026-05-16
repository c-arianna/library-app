package mentoring.acomi.library.domain.books.errors;

import mentoring.acomi.library.domain.common.errors.DomainError;

public class BookNotRegistered extends DomainError {

	private static final long serialVersionUID = -3085803485844983199L;

	private static final String code = "BOOK_NOT_REGISTERED";
	
	public BookNotRegistered(String message) {
		super(code, message);
	}

}
