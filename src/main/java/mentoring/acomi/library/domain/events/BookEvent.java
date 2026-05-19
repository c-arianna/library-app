package mentoring.acomi.library.domain.events;

import mentoring.acomi.library.application.aggregates.AggregateType;

public sealed interface BookEvent extends DomainEvent permits BookStateEvent, BookProcessEvent{
	 @Override public default AggregateType aggregateType() { return AggregateType.BOOK; }
}

