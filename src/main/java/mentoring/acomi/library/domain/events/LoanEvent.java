package mentoring.acomi.library.domain.events;

import mentoring.acomi.library.application.aggregates.AggregateType;

public sealed interface LoanEvent extends DomainEvent 
permits LoanStateEvent, LoanProcessEvent{
	 @Override public default AggregateType aggregateType() { return AggregateType.LOAN; }
}
