package mentoring.acomi.library.domain.events.payload;

public record BookLoanPayload(String isbn, String loanId, String userId) {

}
