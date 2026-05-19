package mentoring.acomi.library.domain.events;

public sealed interface LoanProcessEvent extends LoanEvent permits LoanConfirmRequestedEvent{

}
