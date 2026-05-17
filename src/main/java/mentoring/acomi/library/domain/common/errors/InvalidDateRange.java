package mentoring.acomi.library.domain.common.errors;

public class InvalidDateRange  extends ValidationDomain{

	private static final long serialVersionUID = 5469923943507438825L;

	private static final String code = "INVALID_DATE_RANGE";
	
	public InvalidDateRange(String message) {
		super(code, message);
	}

}
