package mentoring.acomi.library.domain.events.books;

import java.time.Instant;

import mentoring.acomi.library.domain.events.DomainEventType;

public record BookCopiesRemovedEvent(
		String aggregateType,
	    String aggregateId,
	    BookCopiesRemovedPayload payload,
        Instant occurredAt
        )implements BookEvent {
    @Override public DomainEventType type() { return DomainEventType.BookCopiesRemoved; }
}
