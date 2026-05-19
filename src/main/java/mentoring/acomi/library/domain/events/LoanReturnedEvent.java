package mentoring.acomi.library.domain.events;

import java.time.Instant;

import mentoring.acomi.library.domain.events.payload.LoanPayload;

public record LoanReturnedEvent(
		String aggregateId,
	    LoanPayload payload,
        Instant occurredAt
        )implements LoanEvent {
    @Override public DomainEventType type() { return DomainEventType.LoanReturned; }

}