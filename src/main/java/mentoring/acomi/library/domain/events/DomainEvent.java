package mentoring.acomi.library.domain.events;

import java.time.Instant;

import mentoring.acomi.library.application.aggregates.AggregateType;

public sealed interface DomainEvent permits BookEvent, LoanEvent {
	AggregateType aggregateType();
	String aggregateId();
	Instant occurredAt();
	DomainEventType type();
	Object payload();
}
