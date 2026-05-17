package mentoring.acomi.library.domain.events.payload;

public record BookCopiesRemovedPayload(String isbn, int quantity, String reason) {}
