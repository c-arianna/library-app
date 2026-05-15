package mentoring.acomi.library.domain.events.books;

import java.time.Instant;

import mentoring.acomi.library.domain.events.DomainEventType;

public record BookRegisteredEvent(
    String aggregateType,
    String aggregateId,
    BookRegisteredPayload payload,
    Instant occurredAt   
) implements BookEvent {
    @Override public DomainEventType type() { return DomainEventType.BookRegistered; }
}

