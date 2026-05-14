package mentoring.acomi.library.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import mentoring.acomi.library.domain.books.Book;
import mentoring.acomi.library.infrastructure.persistence.entity.BookViewEntity;

@Component
public class BookViewJpaMapper {

	public BookViewEntity toEntity(Book book) {
		return new BookViewEntity(book.getIsbn().getValue(), book.getAuthor().getValue(), 
				book.getTitle().getValue(), book.getDescription().getValue());

	}

}
