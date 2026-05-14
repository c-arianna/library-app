package mentoring.acomi.library.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mentoring.acomi.library.application.aggregates.AggregateFactory;
import mentoring.acomi.library.application.aggregates.BookAggregate;
import mentoring.acomi.library.application.errors.ApplicationConflictError;
import mentoring.acomi.library.application.errors.ValidationError;
import mentoring.acomi.library.application.repositories.EventRepository;
import mentoring.acomi.library.domain.books.Book;
import mentoring.acomi.library.domain.common.errors.ValidationDomainError;
import mentoring.acomi.library.infrastructure.dto.books.AddBookRequest;
import mentoring.acomi.library.infrastructure.dto.books.AddBookResponse;

@Service
public class BookService {

	private final EventRepository eventRepository;
	private final AggregateFactory aggregateFactory;
	
	public BookService(EventRepository eventRepository, AggregateFactory aggregateFactory) {
		this.eventRepository = eventRepository;
		this.aggregateFactory = aggregateFactory;
	}
	
	@Transactional
	public AddBookResponse addBook(AddBookRequest request)  {
				
			String isbn = request.isbn();
		
			if(eventRepository.exists("Book", isbn)) {
				throw new ApplicationConflictError("BOOK_ALREADY_EXISTS", String.format("ISBN: %s", isbn));
			}
			
			try {
				BookAggregate aggregate = aggregateFactory.load(BookAggregate.aggregateType, request.isbn());
				Book book = getBook(request);
				aggregate.register(book);
				return new AddBookResponse(book.getIsbn().formatted());
			}catch (ValidationDomainError e) {
		        throw new ValidationError(e.getCode(), e.getMessage());
		    }
		
	}

	private Book getBook(AddBookRequest request) {
		return Book.create(request.isbn(), request.author(), request.title(), request.description());	
	}
}
