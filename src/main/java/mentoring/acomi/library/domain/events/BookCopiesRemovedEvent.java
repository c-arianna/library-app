package mentoring.acomi.library.domain.events;

import java.time.Instant;

import mentoring.acomi.library.domain.events.payload.BookCopiesRemovedPayload;

public record BookCopiesRemovedEvent(
	    String aggregateId,
	    BookCopiesRemovedPayload payload,
        Instant occurredAt
        )implements BookEvent {
    @Override public DomainEventType type() { return DomainEventType.BookCopiesRemoved; }
}
