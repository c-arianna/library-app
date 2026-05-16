package mentoring.acomi.library.domain.events.books;

import java.time.Instant;

import mentoring.acomi.library.domain.events.DomainEventType;

public record BookCopiesAddedEvent (
		String aggregateType,
	    String aggregateId,
        BookCopiesAddedPayload payload,
        Instant occurredAt
        )implements BookEvent {
    @Override public DomainEventType type() { return DomainEventType.BookCopiesAdded; }
}
