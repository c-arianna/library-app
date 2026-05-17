package mentoring.acomi.library.domain.common.errors;


public class NotFound extends DomainError{

	private static final long serialVersionUID = -3780977437333386519L;

	public NotFound(String code, String message) {
		super(code, message);
	}

}
