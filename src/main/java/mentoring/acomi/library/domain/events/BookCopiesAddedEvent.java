package mentoring.acomi.library.domain.events;

import java.time.Instant;

import mentoring.acomi.library.domain.events.payload.BookCopiesAddedPayload;

public record BookCopiesAddedEvent (
	    String aggregateId,
        BookCopiesAddedPayload payload,
        Instant occurredAt
        )implements BookStateEvent {
    @Override public DomainEventType type() { return DomainEventType.BookCopiesAdded; }
}
