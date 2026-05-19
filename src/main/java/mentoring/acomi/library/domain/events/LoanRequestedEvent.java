package mentoring.acomi.library.domain.events;

import java.time.Instant;

import mentoring.acomi.library.domain.events.payload.LoanRequestPayload;

public record LoanRequestedEvent (
	    String aggregateId,
        LoanRequestPayload payload,
        Instant occurredAt
        )implements LoanStateEvent {
    @Override public DomainEventType type() { return DomainEventType.LoanRequested; }
}