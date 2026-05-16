package mentoring.acomi.library.application.repositories;

import java.util.List;

import mentoring.acomi.library.domain.events.books.BookEvent;

public interface EventRepository {
	public void appendToStream(BookEvent event);
	public List<BookEvent> loadStream(String aggregateType, String aggregateId);
	public boolean exists(String aggregateType, String aggregateId);
	public List<BookEvent> loadAll();
	public BookEvent getEvent(String eventType, String aggregateId);
}
