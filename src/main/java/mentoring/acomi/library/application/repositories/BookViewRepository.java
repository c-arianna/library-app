package mentoring.acomi.library.application.repositories;

import java.util.List;

import mentoring.acomi.library.application.BookFilter;
import mentoring.acomi.library.application.view.BookView;
import mentoring.acomi.library.domain.model.books.Book;

public interface BookViewRepository {
	  public void addBook(Book book);
	  public List<BookView> find(BookFilter filter);
	  public void addCopies(String isbn, int quantity);
}
