package mentoring.acomi.library.domain.events;

import java.time.Instant;

import mentoring.acomi.library.domain.events.payload.LoanPayload;

public record LoanCanceledEvent(
		String aggregateId,
	    LoanPayload payload,
        Instant occurredAt
        )implements LoanStateEvent {
    @Override public DomainEventType type() { return DomainEventType.LoanCanceled; }

}