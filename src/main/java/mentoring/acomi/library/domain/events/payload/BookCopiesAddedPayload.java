package mentoring.acomi.library.domain.events.payload;

public record BookCopiesAddedPayload(String isbn, int quantity) {}
