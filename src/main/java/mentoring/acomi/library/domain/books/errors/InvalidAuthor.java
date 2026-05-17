package mentoring.acomi.library.domain.books.errors;

import mentoring.acomi.library.domain.common.errors.ValidationDomain;

public class InvalidAuthor extends ValidationDomain {

	private static final long serialVersionUID = 1635856226905060585L;

	private static final String type = "INVALID_AUTHOR";
	
	public InvalidAuthor(String message) {
		super(type, message);
	}
	
	public static InvalidAuthor empty() {
	    return new InvalidAuthor("Author is required");
	 }

}
