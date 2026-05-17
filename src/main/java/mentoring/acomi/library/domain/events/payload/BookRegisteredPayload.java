package mentoring.acomi.library.domain.events.payload;

public record BookRegisteredPayload(String isbn, String author, String title, String description) {
}
