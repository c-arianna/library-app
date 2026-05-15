package mentoring.acomi.library.infrastructure.persistence.repositories.adapters;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import mentoring.acomi.library.application.repositories.EventRepository;
import mentoring.acomi.library.domain.events.books.BookEvent;
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
	public void appendToStream(BookEvent event) {

		Optional<Integer> version = repository.findLastVersion(event.aggregateType(), event.aggregateId());

		Integer nextVersion = version.isEmpty() ? 0 : version.get() + 1;

		EventEntity entity = mapper.toEntity(event);
		entity.setEventVersion(nextVersion);

		repository.save(entity);
	}

	@Override
	public List<BookEvent> loadStream(String aggregateType, String aggregateId) {
		return repository.findEventsForAggregate(aggregateType, aggregateId).stream().map(mapper::toDomain).toList();
	}

	@Override
	public boolean exists(String aggregateType, String aggregateId) {
		return repository.existsByAggregateTypeAndAggregateId(aggregateType, aggregateId);
	}

	@Override
	public List<BookEvent> loadAll() {
		return repository.findAll(Sort.by("eventVersion")).stream().map(mapper::toDomain).toList();
	}

	@Override
	public boolean existsEvent(String eventType, String aggregateId) {
		return repository.existsByEventTypeAndAggregateId(eventType, aggregateId);
	}

}
