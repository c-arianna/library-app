package mentoring.acomi.library.application.repositories;

import java.util.List;
import java.util.Optional;

import mentoring.acomi.library.domain.events.DomainEvent;

public interface EventRepository {
	public void appendToStream(DomainEvent event);
	public <E extends DomainEvent> List<E> loadStream(String aggregateType, String aggregateId,  Class<E> eventType);
	public boolean exists(String aggregateType, String aggregateId);
	public List<DomainEvent> loadAll();
	public Optional<DomainEvent> getEvent(String eventType, String aggregateId);
}
