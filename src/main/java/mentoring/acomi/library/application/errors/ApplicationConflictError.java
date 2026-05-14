package mentoring.acomi.library.application.errors;

import lombok.Getter;

@Getter
public class ApplicationConflictError extends RuntimeException {

	private static final long serialVersionUID = 824313013090108769L;
	
	private String type;
	private String code;
	
	public ApplicationConflictError(String code, String message) {
		super(message);
		type =  "CONFLICT";
		this.code = code;
	}
	
}
