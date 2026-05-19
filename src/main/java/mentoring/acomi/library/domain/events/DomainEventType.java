package mentoring.acomi.library.domain.events;

public enum DomainEventType {
	BookRegistered, BookCopiesAdded, BookCopiesRemoved, BookReserved, BookBorrowed, BookReleased, BookReturned,
	LoanRequested, LoanFailed, LoanReserved, LoanConfirmed, LoanCanceled, LoanReturned
}
