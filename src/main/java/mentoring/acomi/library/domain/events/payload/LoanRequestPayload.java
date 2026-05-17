package mentoring.acomi.library.domain.events.payload;

import mentoring.acomi.library.domain.common.DateRange;
import mentoring.acomi.library.domain.model.loans.LoanStatus;

public record LoanRequestPayload(String id, String isbn, String userId, DateRange period, LoanStatus status) {

}
