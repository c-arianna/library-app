package mentoring.acomi.library.infrastructure.persistence.repositories.adapters;

import org.springframework.stereotype.Repository;

import mentoring.acomi.library.application.repositories.BookViewRepository;
import mentoring.acomi.library.domain.books.Book;
import mentoring.acomi.library.infrastructure.persistence.entity.BookViewEntity;
import mentoring.acomi.library.infrastructure.persistence.mapper.BookViewJpaMapper;
import mentoring.acomi.library.infrastructure.persistence.repositories.BookViewJpaRepository;

@Repository
public class JpaBookViewRepositoryAdapter implements BookViewRepository {

	private final BookViewJpaRepository repository;
	private final BookViewJpaMapper mapper;
	
	public JpaBookViewRepositoryAdapter(BookViewJpaRepository repository, BookViewJpaMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}
	
	@Override
	public void addBook(Book book) {
		BookViewEntity entity = mapper.toEntity(book);
		repository.save(entity);
	}

}
