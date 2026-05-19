package mentoring.acomi.library.application;

import mentoring.acomi.library.domain.model.loans.LoanStatus;

public record LoanFilter(String isbn, String userId, LoanStatus status) {

}
