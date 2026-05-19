package mentoring.acomi.library.domain.events;

import mentoring.acomi.library.application.aggregates.AggregateType;

public sealed interface LoanEvent extends DomainEvent 
permits LoanRequestedEvent, LoanFailedEvent, LoanReservedEvent, LoanConfirmedEvent, LoanCanceledEvent, 
LoanReturnedEvent {
	 @Override public default AggregateType aggregateType() { return AggregateType.LOAN; }
}
