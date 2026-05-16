package mentoring.acomi.library.domain.events.books;

public record BookCopiesRemovedPayload(String isbn, int quantity, String reason) {}
