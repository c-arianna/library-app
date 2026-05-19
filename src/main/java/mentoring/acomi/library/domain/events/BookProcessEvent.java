package mentoring.acomi.library.domain.events;

public sealed interface BookProcessEvent extends BookEvent permits BookReservationRejectedEvent, BookBorrowRejectedEvent{}
