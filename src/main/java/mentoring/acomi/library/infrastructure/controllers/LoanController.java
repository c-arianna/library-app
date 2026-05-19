package mentoring.acomi.library.infrastructure.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mentoring.acomi.library.application.LoanFilter;
import mentoring.acomi.library.application.services.LoanService;
import mentoring.acomi.library.domain.model.loans.LoanStatus;
import mentoring.acomi.library.infrastructure.dto.loans.AddLoanRequest;
import mentoring.acomi.library.infrastructure.dto.loans.LoanResponse;
import mentoring.acomi.library.infrastructure.dto.loans.LoansResponse;

@RestController
@RequestMapping("/loans")
public class LoanController {

	private final LoanService service;

	public LoanController(LoanService service) {
		this.service = service;
	}

	@PostMapping
	public LoanResponse addLoan(@RequestBody @Valid AddLoanRequest request) {
		return service.addLoan(request);
	}

	@PostMapping("/{loanId}/confirm")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void confirmLoan(@PathVariable String loanId) {
		service.confirmLoan(loanId);
	}
	
	@PostMapping("/{loanId}/reject")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void cancelLoan(@PathVariable String loanId) {
		service.cancelLoan(loanId);
	}
	
	@PostMapping("/{loanId}/return")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void returnLoan(@PathVariable String loanId) {
		service.returnLoan(loanId);
	}
	
	@GetMapping
	public LoansResponse findLoans(@RequestParam(required = false) String userId, @RequestParam(required = false) String isbn,
		    @RequestParam(required = false) LoanStatus status) {
	    LoanFilter loanFilter = new LoanFilter(isbn, userId, status);
		return service.findLoans(loanFilter);
	}

}
