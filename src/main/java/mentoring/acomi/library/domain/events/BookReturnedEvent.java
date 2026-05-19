package mentoring.acomi.library.domain.events;

import java.time.Instant;

import mentoring.acomi.library.domain.events.payload.BookLoanPayload;

public record BookReturnedEvent(
		String aggregateId,
	    BookLoanPayload payload,
        Instant occurredAt
        )implements BookEvent {
    @Override public DomainEventType type() { return DomainEventType.BookReturned; }
}