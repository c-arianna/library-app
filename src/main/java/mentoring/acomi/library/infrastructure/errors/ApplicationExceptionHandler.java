package mentoring.acomi.library.infrastructure.errors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import mentoring.acomi.library.application.errors.ApplicationConflictError;
import mentoring.acomi.library.application.errors.ValidationError;
import mentoring.acomi.library.infrastructure.errors.dto.ErrorResponse;

@RestControllerAdvice
public class ApplicationExceptionHandler {

	private final Logger logger;

	public ApplicationExceptionHandler(@Value("${spring.application.name}") String applicationName) {
		this.logger = LogManager.getLogger(applicationName);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleFieldValidation(MethodArgumentNotValidException e) throws Exception {

		FieldError fieldError = e.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
		String message = fieldError != null ? fieldError.getDefaultMessage() : "Dati di input non validi";

		return handleException(e, "VALIDATION_ERROR", message, "VALIDATION_ERROR");

	}

	@ExceptionHandler(ValidationError.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationError(ValidationError e)
			throws Exception {

		return handleException(e, e.getCode(), e.getMessage(), e.getType());

	}
	
	@ExceptionHandler(ApplicationConflictError.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleConflictError(ApplicationConflictError e) throws Exception {

		return handleException(e, e.getCode(), e.getMessage(), e.getType());

	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ErrorResponse handleGenericException(Exception e) throws Exception {

		logger.error("Unexpected exception, {}", e.getMessage(), e);

		String code = "INTERNAL_SERVER_ERROR";
		String message = "Generic error";
		String type = "INTERNAL_ERROR";

		return handleException(e, code, message, type);

	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Throwable.class)
	public ErrorResponse handleThrowable(Throwable e) throws Exception {

		logger.error("Fatal error, {}", e.getMessage(), e);
		
		String code = "INTERNAL_SERVER_ERROR";
		String message = "Fatal Error";
		String type = "INTERNAL_ERROR";

		return new ErrorResponse(code, message, type);

	}

	private ErrorResponse handleException(Exception e, String code, String message, String type) throws Exception {

		if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
			throw e;
		}

		return new ErrorResponse(code, message, type);

	}

}
