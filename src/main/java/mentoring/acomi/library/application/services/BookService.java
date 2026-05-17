package mentoring.acomi.library.application.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mentoring.acomi.library.application.BookFilter;
import mentoring.acomi.library.application.aggregates.AggregateFactory;
import mentoring.acomi.library.application.aggregates.BookAggregate;
import mentoring.acomi.library.application.repositories.BookViewRepository;
import mentoring.acomi.library.application.repositories.EventRepository;
import mentoring.acomi.library.application.view.BookView;
import mentoring.acomi.library.domain.common.errors.ApplicationConflict;
import mentoring.acomi.library.domain.model.books.Book;
import mentoring.acomi.library.infrastructure.dto.books.AddBookRequest;
import mentoring.acomi.library.infrastructure.dto.books.BookResponse;
import mentoring.acomi.library.infrastructure.dto.books.AddBookCopiesRequest;
import mentoring.acomi.library.infrastructure.dto.books.BookDto;
import mentoring.acomi.library.infrastructure.dto.books.BooksResponse;
import mentoring.acomi.library.infrastructure.dto.books.RemoveBookCopiesRequest;

@Service
public class BookService {

	private final EventRepository eventRepository;
	private final AggregateFactory aggregateFactory;
	private final BookViewRepository bookViewRepository;

	public BookService(EventRepository eventRepository, AggregateFactory aggregateFactory,
			BookViewRepository bookViewRepository) {
		this.eventRepository = eventRepository;
		this.aggregateFactory = aggregateFactory;
		this.bookViewRepository = bookViewRepository;
	}

	@Transactional
	public BookResponse addBook(AddBookRequest request) {

		String isbn = request.isbn();

		if (eventRepository.exists("Book", isbn)) {
			throw new ApplicationConflict("BOOK_ALREADY_EXISTS", String.format("ISBN: %s", isbn));
		}

		BookAggregate aggregate = aggregateFactory.loadBook(request.isbn());
		Book book = getBook(request);
		aggregate.register(book);
		return new BookResponse(book.getIsbn().formatted());

	}

	public BooksResponse findBooks(BookFilter filter) {
		List<BookView> books = bookViewRepository.find(filter);
		return toBooksResponse(books);
	}

	@Transactional
	public void addBookCopies(AddBookCopiesRequest request, String isbn) {
		BookAggregate aggregate = aggregateFactory.loadBook(isbn);
		aggregate.addCopies(request.quantity());
	}

	@Transactional
	public void removeBookCopies(RemoveBookCopiesRequest request, String isbn) {
		BookAggregate aggregate = aggregateFactory.loadBook(isbn);
		aggregate.removeCopies(request.quantity(), request.reason());
	}
	
	private Book getBook(AddBookRequest request) {
		return Book.create(request.isbn(), request.author(), request.title(), request.description());
	}

	private BooksResponse toBooksResponse(List<BookView> books) {

		List<BookDto> bookResponse = books.stream().map(b -> new BookDto(b.isbn(), b.author(), b.title(),
				b.description(), b.availableCopies() > 0)).toList();

		return new BooksResponse(bookResponse);
	}
}
