package mentoring.acomi.library.domain.events.payload;

import mentoring.acomi.library.application.BookBorrowRejectReason;

public record BookBorrowRejectedPayload(String isbn, String loanId, String userId, BookBorrowRejectReason reason) {}
