package mentoring.acomi.library.application.errors;

public class EventStoreInconsistencyException extends RuntimeException {

	private static final long serialVersionUID = -8447083567671107575L;
	
	public EventStoreInconsistencyException(String message) {
		super(message);
	}

}
