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
import mentoring.acomi.library.domain.books.errors.BookNotRegistered;
import mentoring.acomi.library.domain.books.errors.CannotRemoveBookCopies;
import mentoring.acomi.library.domain.books.errors.InvalidQuantity;
import mentoring.acomi.library.domain.common.errors.ValidationDomainError;
import mentoring.acomi.library.infrastructure.errors.dto.ErrorResponse;

@RestControllerAdvice
public class ApplicationExceptionHandler {

	private final Logger logger;

	public ApplicationExceptionHandler(@Value("${spring.application.name}") String applicationName) {
		this.logger = LogManager.getLogger(applicationName);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationErorr(MethodArgumentNotValidException e) throws Exception {

		FieldError fieldError = e.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
		String message = fieldError != null ? fieldError.getDefaultMessage() : "Dati di input non validi";

		return handleException(e, "VALIDATION_ERROR", message, "VALIDATION_ERROR");

	}

	@ExceptionHandler(ValidationDomainError.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationDomainError(ValidationDomainError e)
			throws Exception {

		return handleException(e, e.getCode(), e.getMessage(), "VALIDATION_ERROR");
	}
	
	@ExceptionHandler(ApplicationConflictError.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleConflictError(ApplicationConflictError e) throws Exception {
		return handleException(e, e.getCode(), e.getMessage(), e.getType());
	}
		
	@ExceptionHandler(InvalidQuantity.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
	public ErrorResponse handleInvalidQuantityError(InvalidQuantity e) throws Exception {
		return handleException(e, e.getCode(), e.getMessage(), "AGGREGATE_INVARIANT_FAILED");
	}
	
	@ExceptionHandler(BookNotRegistered.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
	public ErrorResponse handleBookNotRegisteredError(BookNotRegistered e) throws Exception {
		return handleException(e, e.getCode(), e.getMessage(), "AGGREGATE_INVARIANT_FAILED");
	}
	
	@ExceptionHandler(CannotRemoveBookCopies.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
	public ErrorResponse handleCannotRemoveBookCopiesError(CannotRemoveBookCopies e) throws Exception {
		return handleException(e, e.getCode(), e.getMessage(), "AGGREGATE_INVARIANT_FAILED");
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
