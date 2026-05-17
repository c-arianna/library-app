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

import mentoring.acomi.library.application.errors.EventStoreInconsistencyException;
import mentoring.acomi.library.domain.books.errors.BookNotRegistered;
import mentoring.acomi.library.domain.books.errors.CannotRemoveBookCopies;
import mentoring.acomi.library.domain.books.errors.InvalidQuantity;
import mentoring.acomi.library.domain.common.errors.ApplicationConflict;
import mentoring.acomi.library.domain.common.errors.NotFound;
import mentoring.acomi.library.domain.common.errors.ValidationDomain;
import mentoring.acomi.library.domain.loans.errors.BookNotAvailable;
import mentoring.acomi.library.domain.loans.errors.LoanNotExist;
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
		String message = fieldError != null ? fieldError.getDefaultMessage() : "Invalid data input";

		return handleException(e, "VALIDATION_ERROR", message, "VALIDATION_ERROR");

	}

	@ExceptionHandler(ValidationDomain.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationDomainError(ValidationDomain e) throws Exception {

		return handleException(e, e.getCode(), e.getMessage(), "VALIDATION_ERROR");
	}

	@ExceptionHandler(ApplicationConflict.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleConflictError(ApplicationConflict e) throws Exception {
		return handleException(e, e.getCode(), e.getMessage(), "CONFLICT");
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

	@ExceptionHandler(NotFound.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleNotFoundError(NotFound e) throws Exception {
		return handleException(e, e.getCode(), e.getMessage(), "RESOURCE_NOT_FOUND");
	}

	@ExceptionHandler(BookNotAvailable.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
	public ErrorResponse handleBookNotAvailableError(BookNotAvailable e) throws Exception {
		return handleException(e, e.getCode(), e.getMessage(), "BOOK_NOT_AVAILABLE");
	}

	@ExceptionHandler(EventStoreInconsistencyException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleEventStoreInconsistency(EventStoreInconsistencyException e) throws Exception{
		return handleException(e, "EVENT_STORE_INCONSISTENCY", e.getMessage(), "EVENT_STORE_INCONSISTENCY");
	}

	@ExceptionHandler(LoanNotExist.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
	public ErrorResponse handleLoanNotCreatedError(LoanNotExist e) throws Exception {
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
