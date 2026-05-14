package mentoring.acomi.library.domain.common.errors;

import lombok.Getter;

@Getter
public class ValidationDomainError extends DomainError{
	
	private static final long serialVersionUID = -8967459678463791707L;
	
	public ValidationDomainError(String code, String message) {
		super(code, message);
	}

}
