package mentoring.acomi.library.infrastructure.dto.loans;

import java.time.LocalDate;

import mentoring.acomi.library.domain.model.loans.LoanStatus;

public record LoanDto(String id, String isbn, String userId, LoanStatus status, LocalDate start, LocalDate end) {

}
