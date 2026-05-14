package mentoring.acomi.library.infrastructure.persistence.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mentoring.acomi.library.infrastructure.persistence.entity.EventEntity;

@Repository
public interface EventJpaRepository extends JpaRepository<EventEntity, Long> {

	@Query("""
			select max(e.eventVersion)
			from EventEntity e
			where e.aggregateType = :aggregateType and e.aggregateId = :aggregateId
			""")
	Optional<Integer> findLastVersion(@Param("aggregateType") String aggregateType,
			@Param("aggregateId") String aggregateId);

	@Query("""
			    select e
			    from EventEntity e
			    where e.aggregateType = :aggregateType
			      and e.aggregateId = :aggregateId
			    order by e.eventVersion asc
			""")
	List<EventEntity> findEventsForAggregate(@Param("aggregateType") String aggregateType,
			@Param("aggregateId") String aggregateId);

	boolean existsByAggregateTypeAndAggregateId(String aggregateType, String aggregateId);
	
	boolean existsByEventTypeAndAggregateId(String eventType, String aggregateId);

}
