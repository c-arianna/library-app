package mentoring.acomi.library.application.repositories;

import java.util.List;

import mentoring.acomi.library.domain.events.BookRegistered;

public interface EventRepository {
	public void appendToStream(BookRegistered event);
	public List<BookRegistered> loadStream(String aggregateType, String aggregateId);
	public boolean exists(String aggregateType, String aggregateId);
	public List<BookRegistered> loadAll();
	public boolean existsEvent(String eventType, String aggregateId);
}
