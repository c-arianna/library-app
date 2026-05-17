package mentoring.acomi.library.application.view;

import java.time.LocalDate;

import mentoring.acomi.library.domain.model.loans.LoanStatus;

public record LoanView(String id, String isbn, String userId, LocalDate start, LocalDate end, LoanStatus status) {

}
