package mentoring.acomi.library.domain.events.books;

import java.time.Instant;

import mentoring.acomi.library.domain.events.DomainEventType;


public sealed interface BookEvent permits BookRegisteredEvent, BookCopyAddedEvent {
    String aggregateType();
    String aggregateId();
    Instant occurredAt();
    DomainEventType type();
    Object payload();
}

