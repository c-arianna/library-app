package mentoring.acomi.library.domain.books;

import mentoring.acomi.library.domain.books.errors.InvalidAuthor;
import mentoring.acomi.library.domain.common.ValueObject;

public class Author extends ValueObject<String> {

	public Author(String value)  {
		super(value);
	}

	@Override
	public String validate(String author) {

		if (author == null || author.trim().length() == 0) {
			throw InvalidAuthor.empty();
		}

		return author.trim();

	}

}
