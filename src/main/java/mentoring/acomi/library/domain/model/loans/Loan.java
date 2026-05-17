package mentoring.acomi.library.domain.model.loans;

import java.time.LocalDate;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mentoring.acomi.library.domain.common.DateRange;
import mentoring.acomi.library.domain.model.books.ISBN;

@EqualsAndHashCode
@ToString
@Builder
public class Loan {
	
	private LoanIdentifier id;
	private ISBN isbn;
	private String userId;
	private LoanStatus status;
	private DateRange period;
	
	private Loan(LoanIdentifier id, ISBN isbn, String userId, LoanStatus status, DateRange period) {
        this.id = id;
        this.isbn = isbn;
        this.userId = userId;
        this.status = status;
        this.period = period;
    }
	
	public static Loan create(LoanIdentifier id, String isbn, String userId, LocalDate start, LocalDate end) {
		DateRange period = new DateRange(start, end);
        return new Loan(id, ISBN.of(isbn), userId, LoanStatus.PENDING, period);
    }
	
	public String getId() {
		return id.getValue();
	}
	
	public String getIsbn() {
    	return isbn!= null ? isbn.getValue() : "";
    }
	
	public String getUserId() {
		return userId;
	}
	
	public LoanStatus getStatus() {
		return status;
	}
	
	public DateRange getPeriod() {
		return period;
	}
	

}
