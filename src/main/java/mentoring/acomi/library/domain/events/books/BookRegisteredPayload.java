package mentoring.acomi.library.domain.events.books;

public record BookRegisteredPayload(String isbn, String author, String title, String description) {
}
