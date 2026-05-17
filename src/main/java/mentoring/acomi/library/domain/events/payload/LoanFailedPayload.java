package mentoring.acomi.library.domain.events.payload;

import mentoring.acomi.library.application.LoanFailedReason;

public record LoanFailedPayload(String id, LoanFailedReason reason) {

}
