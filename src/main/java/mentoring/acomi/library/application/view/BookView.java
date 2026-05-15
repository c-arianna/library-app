package mentoring.acomi.library.application.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookView {

	private String isbn;
	private String author;
	private String title;
	private String description;

	private int totalCopies;
	private int availableCopies;
	private int borrowedCopies;
	private int reservedCopies;

}
