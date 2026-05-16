package mentoring.acomi.library.domain.events.books;

public record BookCopiesAddedPayload(String isbn, int quantity) {}
