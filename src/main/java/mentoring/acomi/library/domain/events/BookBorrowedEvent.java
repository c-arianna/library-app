package mentoring.acomi.library.domain.events;

import java.time.Instant;

import mentoring.acomi.library.domain.events.payload.BookLoanPayload;

public record BookBorrowedEvent(
		String aggregateId,
	    BookLoanPayload payload,
        Instant occurredAt
        )implements BookStateEvent {
    @Override public DomainEventType type() { return DomainEventType.BookBorrowed; }
}
