package mentoring.acomi.library.domain.events;

import java.time.Instant;

import mentoring.acomi.library.domain.events.payload.BookBorrowRejectedPayload;

public record BookBorrowRejectedEvent(
		String aggregateId,
		BookBorrowRejectedPayload payload,
        Instant occurredAt
        )implements BookProcessEvent {
    @Override public DomainEventType type() { return DomainEventType.BookBorrowRejected; }
}
