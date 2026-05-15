package mentoring.acomi.library.domain.events.books;

import java.time.Instant;

import mentoring.acomi.library.domain.events.DomainEventType;

public record BookCopyAddedEvent (
		String aggregateType,
	    String aggregateId,
        BookCopyAddedPayload payload,
        Instant occurredAt
        )implements BookEvent {
    @Override public DomainEventType type() { return DomainEventType.BookCopyAdded; }
}
