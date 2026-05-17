package mentoring.acomi.library.infrastructure.persistence.repositories.adapters;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import mentoring.acomi.library.application.repositories.EventRepository;
import mentoring.acomi.library.domain.events.DomainEvent;
import mentoring.acomi.library.infrastructure.persistence.entity.EventEntity;
import mentoring.acomi.library.infrastructure.persistence.mapper.EventJpaMapper;
import mentoring.acomi.library.infrastructure.persistence.repositories.EventJpaRepository;

@Repository
public class JpaEventRepositoryAdapter implements EventRepository {

	private final EventJpaRepository repository;
	private final EventJpaMapper mapper;

	public JpaEventRepositoryAdapter(EventJpaRepository repository, EventJpaMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public void appendToStream(DomainEvent event) {

		Optional<Integer> version = repository.findLastVersion(event.aggregateType().name(), event.aggregateId());

		Integer nextVersion = version.isEmpty() ? 0 : version.get() + 1;

		EventEntity entity = mapper.toEntity(event);
		entity.setEventVersion(nextVersion);

		repository.save(entity);
	}

	@Override
	public <E extends DomainEvent> List<E> loadStream(String aggregateType, String aggregateId, Class<E> eventType) {
		return repository.findEventsForAggregate(aggregateType, aggregateId).stream().map(mapper::toDomain)
				.map(event -> {
					if (!eventType.isInstance(event)) {
						throw new IllegalStateException(String.format("Unexpected event type: %s", event.getClass().getSimpleName()));
					}
					return eventType.cast(event);
				}).toList();
	}

	@Override
	public boolean exists(String aggregateType, String aggregateId) {
		return repository.existsByAggregateTypeAndAggregateId(aggregateType, aggregateId);
	}

	@Override
	public List<DomainEvent> loadAll() {
		return repository.findAll(Sort.by("eventVersion")).stream().map(mapper::toDomain).toList();
	}

	@Override
	public Optional<DomainEvent> getEvent(String eventType, String aggregateId) {
		Optional<EventEntity> event = repository.getByEventTypeAndAggregateId(eventType, aggregateId);

		return event.isEmpty() ? Optional.empty() : Optional.of(mapper.toDomain(event.get()));
	}

}
