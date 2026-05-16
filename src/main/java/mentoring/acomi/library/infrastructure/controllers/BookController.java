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
import mentoring.acomi.library.application.BookFilter;
import mentoring.acomi.library.application.services.BookService;
import mentoring.acomi.library.infrastructure.dto.books.AddBookRequest;
import mentoring.acomi.library.infrastructure.dto.books.BookResponse;
import mentoring.acomi.library.infrastructure.dto.books.AddBookCopiesRequest;
import mentoring.acomi.library.infrastructure.dto.books.BooksResponse;
import mentoring.acomi.library.infrastructure.dto.books.RemoveBookCopiesRequest;

@RestController
@RequestMapping("/books")
public class BookController {

	private final BookService service;
	
	public BookController(BookService service) {
		this.service = service;		
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookResponse addBook(@RequestBody @Valid AddBookRequest request) {
		return service.addBook(request);
	}
	
	@GetMapping
	public BooksResponse findBooks(@RequestParam(required = false) String title, 
			@RequestParam(required = false) String author, @RequestParam(required = false) String isbn,
		    @RequestParam(required = false) boolean onlyAvailable){
		BookFilter filter = new BookFilter(title, author, isbn, onlyAvailable);
		return service.findBooks(filter);
	}
	
	@PostMapping("/{isbn}/copies/add")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void addBookCopies(@RequestBody AddBookCopiesRequest request, @PathVariable String isbn) {
		service.addBookCopies(request, isbn);
	}
	
	@PostMapping("/{isbn}/copies/remove")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeBookCopies(@RequestBody RemoveBookCopiesRequest request, @PathVariable String isbn) {
		service.removeBookCopies(request, isbn);
	}
}
