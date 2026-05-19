package mentoring.acomi.library.domain.events;

public enum DomainEventType {
	BookRegistered, BookCopiesAdded, BookCopiesRemoved, BookReserved, BookBorrowed, BookReleased, BookReturned, 
	BookReservationRejected, BookBorrowRejected, LoanRequested, LoanFailed, LoanReserved, LoanConfirmed, LoanCanceled, LoanReturned,
	LoanConfirmRequested
}
