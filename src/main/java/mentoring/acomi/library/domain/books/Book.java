package mentoring.acomi.library.domain.books;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@Builder
public class Book {
    
	private final ISBN isbn;
    private final Author author;
    private final Title title;
    private final Description description;
    
    private Book(ISBN isbn, Author author, Title title, Description description) {
    	this.isbn = isbn;
    	this.author = author;
    	this.title = title;
    	this.description = description;	
    }
    
    public static Book create(String isbn, String author, String title, String description)   {
    	return new Book(ISBN.of(isbn), new Author(author), new Title(title), new Description(description));
    }
}

