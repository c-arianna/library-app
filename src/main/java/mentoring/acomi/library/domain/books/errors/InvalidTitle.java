package mentoring.acomi.library.domain.books.errors;

import mentoring.acomi.library.domain.common.errors.ValidationDomainError;

public class InvalidTitle extends ValidationDomainError {

	private static final long serialVersionUID = 1635856226905060585L;

	private static final String type = "INVALID_TITLE";
	
	public InvalidTitle(String message) {
		super(type, message);
	}
	
	public static InvalidTitle empty() {
	    return new InvalidTitle("Title is required");
	 }

}
