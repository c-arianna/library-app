package mentoring.acomi.library.domain.events;

public sealed interface LoanStateEvent extends LoanEvent permits LoanRequestedEvent, LoanFailedEvent, LoanReservedEvent, LoanConfirmedEvent, LoanCanceledEvent, 
LoanReturnedEvent{}
