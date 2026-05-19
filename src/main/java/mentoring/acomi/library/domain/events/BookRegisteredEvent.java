package mentoring.acomi.library.domain.events;

import java.time.Instant;

import mentoring.acomi.library.domain.events.payload.BookRegisteredPayload;

public record BookRegisteredEvent(
    String aggregateId,
    BookRegisteredPayload payload,
    Instant occurredAt   
) implements BookStateEvent {
    @Override public DomainEventType type() { return DomainEventType.BookRegistered; }
}

