package mentoring.acomi.library.application.errors;

import lombok.Getter;

@Getter
public class ValidationError extends RuntimeException {

	private static final long serialVersionUID = 2797692867101453457L;
	
	private String type;
	private String code;
	
	public ValidationError(String code, String message) {
		super(message);
		type = "VALIDATION_ERROR";
		this.code = code;
		
	}

}
