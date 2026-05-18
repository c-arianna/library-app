package mentoring.acomi.library.domain.events;

public enum DomainEventType {
	BookRegistered, BookCopiesAdded, BookCopiesRemoved, BookReserved, BookBorrowed, BookReleased,
	LoanRequested, LoanFailed, LoanReserved, LoanConfirmed, LoanCanceled
}
