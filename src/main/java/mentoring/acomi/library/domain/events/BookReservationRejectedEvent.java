package mentoring.acomi.library.domain.events;

import java.time.Instant;

import mentoring.acomi.library.domain.events.payload.BookReservationRejectedPayload;

public record BookReservationRejectedEvent(
		String aggregateId,
		BookReservationRejectedPayload payload,
        Instant occurredAt
        )implements BookProcessEvent {
    @Override public DomainEventType type() { return DomainEventType.BookReservationRejected; }
}