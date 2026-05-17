package mentoring.acomi.library.domain.loans.errors;

import mentoring.acomi.library.domain.common.errors.NotFound;

public class BookNotFound extends NotFound{

	private static final long serialVersionUID = 4352324420424827605L;
	
	private static final String code = "BOOK_NOT_FOUND";
	
	public BookNotFound(String message) {
		super(code, message);
	}

}
