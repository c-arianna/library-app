package mentoring.acomi.library.application.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mentoring.acomi.library.application.BookFilter;
import mentoring.acomi.library.application.aggregates.AggregateFactory;
import mentoring.acomi.library.application.aggregates.BookAggregate;
import mentoring.acomi.library.application.errors.ApplicationConflictError;
import mentoring.acomi.library.application.repositories.BookViewRepository;
import mentoring.acomi.library.application.repositories.EventRepository;
import mentoring.acomi.library.application.view.BookView;
import mentoring.acomi.library.domain.model.books.Book;
import mentoring.acomi.library.infrastructure.dto.books.AddBookRequest;
import mentoring.acomi.library.infrastructure.dto.books.BookResponse;
import mentoring.acomi.library.infrastructure.dto.books.AddCopyBookRequest;
import mentoring.acomi.library.infrastructure.dto.books.BookDto;
import mentoring.acomi.library.infrastructure.dto.books.BooksResponse;

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
			throw new ApplicationConflictError("BOOK_ALREADY_EXISTS", String.format("ISBN: %s", isbn));
		}

		BookAggregate aggregate = aggregateFactory.load(BookAggregate.aggregateType, request.isbn());
		Book book = getBook(request);
		aggregate.register(book);
		return new BookResponse(book.getIsbn().formatted());

	}

	public BooksResponse findBooks(BookFilter filter) {
		List<BookView> books = bookViewRepository.find(filter);
		return toBooksResponse(books);
	}

	@Transactional
	public BookResponse addBookCopy(AddCopyBookRequest request) {

		BookAggregate aggregate = aggregateFactory.load(BookAggregate.aggregateType, request.isbn());
		aggregate.addCopy(request.quantity());

		return new BookResponse(request.isbn());
	}

	private Book getBook(AddBookRequest request) {
		return Book.create(request.isbn(), request.author(), request.title(), request.description());
	}

	private BooksResponse toBooksResponse(List<BookView> books) {

		List<BookDto> bookResponse = books.stream().map(b -> new BookDto(b.getIsbn(), b.getAuthor(), b.getTitle(),
				b.getDescription(), b.getAvailableCopies() > 0)).toList();

		return new BooksResponse(bookResponse);
	}
}
