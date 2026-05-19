package mentoring.acomi.library.domain.events;

import java.time.Instant;

import mentoring.acomi.library.domain.events.payload.LoanFailedPayload;

public record LoanFailedEvent(
	    String aggregateId,
	    LoanFailedPayload payload,
        Instant occurredAt
        )implements LoanStateEvent {
    @Override public DomainEventType type() { return DomainEventType.LoanFailed; }
}
