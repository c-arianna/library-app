package mentoring.acomi.library.domain.users.errors;

import mentoring.acomi.library.domain.common.errors.ValidationDomain;

public class InvalidUser extends ValidationDomain {

	private static final long serialVersionUID = -8187807236798901873L;

	private static final String type = "INVALID_USER";

	public InvalidUser(String message) {
		super(type, message);
	}

	public static InvalidUser tooShort() {
		return new InvalidUser("User should be at least 3 character long");
	}

	public static InvalidUser tooLong() {
		return new InvalidUser("User should be at most 100 character long");
	}

	public static InvalidUser empty() {
		return new InvalidUser("User is required");
	}

}
