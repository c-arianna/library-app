package mentoring.acomi.library.application.eventhandler;

import java.util.function.Consumer;
import mentoring.acomi.library.domain.events.BookRegistered;

public interface EventDispatcher {
	public void dispatch(BookRegistered event);
	public void subscribe(String eventType, Consumer<BookRegistered> callback);
}
