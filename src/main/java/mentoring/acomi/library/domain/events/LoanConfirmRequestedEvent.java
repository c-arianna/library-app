package mentoring.acomi.library.domain.events;

import java.time.Instant;

import mentoring.acomi.library.domain.events.payload.LoanPayload;

public record LoanConfirmRequestedEvent(
	    String aggregateId,
	    LoanPayload payload,
        Instant occurredAt
        )implements LoanProcessEvent {
    @Override public DomainEventType type() { return DomainEventType.LoanConfirmRequested; }
}