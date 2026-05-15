package mentoring.acomi.library.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import mentoring.acomi.library.application.view.BookView;
import mentoring.acomi.library.domain.model.books.Book;
import mentoring.acomi.library.infrastructure.persistence.entity.BookViewEntity;

@Component
public class BookViewJpaMapper {

	public BookViewEntity toEntity(Book book) {
		return new BookViewEntity(book.getIsbn(), book.getAuthor(), book.getTitle(), book.getDescription());
	}
	
	public BookView toView(BookViewEntity entity) {
		
		if(entity == null) {
			return null;
		}
		
		return new BookView(entity.getIsbn(), entity.getAuthor(), entity.getTitle(), entity.getDescription(),
				entity.getTotalCopies(), entity.getAvailableCopies(), entity.getBorrowedCopies(),
				entity.getReservedCopies());	
	}

}
