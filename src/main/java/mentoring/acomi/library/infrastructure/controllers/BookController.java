package mentoring.acomi.library.infrastructure.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mentoring.acomi.library.application.services.BookService;
import mentoring.acomi.library.infrastructure.dto.books.AddBookRequest;
import mentoring.acomi.library.infrastructure.dto.books.AddBookResponse;

@RestController
@RequestMapping("/books")
public class BookController {

	private final BookService service;
	
	public BookController(BookService service) {
		this.service = service;		
	}
	
	@PostMapping
	public AddBookResponse addBook(@RequestBody @Valid AddBookRequest request) {
		return this.service.addBook(request);
	}
}
