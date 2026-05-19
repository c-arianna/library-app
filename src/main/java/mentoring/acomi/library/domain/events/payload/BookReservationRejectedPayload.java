package mentoring.acomi.library.domain.events.payload;

import mentoring.acomi.library.application.BookReservationRejectReason;

public record BookReservationRejectedPayload(String isbn, String loanId, String userId, BookReservationRejectReason reason) {}
