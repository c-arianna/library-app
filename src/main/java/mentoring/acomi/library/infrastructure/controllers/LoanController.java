package mentoring.acomi.library.infrastructure.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mentoring.acomi.library.application.services.LoanService;
import mentoring.acomi.library.infrastructure.dto.loans.AddLoanRequest;
import mentoring.acomi.library.infrastructure.dto.loans.LoanResponse;

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
	

}
