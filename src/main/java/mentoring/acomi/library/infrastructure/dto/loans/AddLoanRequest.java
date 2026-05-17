package mentoring.acomi.library.infrastructure.dto.loans;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddLoanRequest(
		
		@NotBlank
		String isbn, 
		
		@NotBlank
		String userId, 
		
		@NotNull
		LocalDate startDate, 
		
		LocalDate endDate) {
	
}
