package mentoring.acomi.library.domain.loans.errors;

import mentoring.acomi.library.domain.common.errors.NotFound;

public class UserNotFound extends NotFound{

	private static final long serialVersionUID = -923356115738897229L;
	
	private static final String code = "USER_NOT_FOUND";
	
	public UserNotFound(String message) {
		super(code, message);
	}

}
