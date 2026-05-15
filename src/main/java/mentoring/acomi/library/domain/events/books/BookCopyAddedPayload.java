package mentoring.acomi.library.domain.events.books;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookCopyAddedPayload {
	
	private String isbn;
	private int quantity;

}
