package mentoring.acomi.library.domain.model.books;

import mentoring.acomi.library.domain.books.errors.InvalidTitle;
import mentoring.acomi.library.domain.common.ValueObject;

public class Title extends ValueObject<String> {

	public Title(String value)  {
		super(value);
	}

	@Override
	public String validate(String title) {

		if (title == null || title.trim().length() == 0) {
			throw InvalidTitle.empty();
		}

		return title.trim();
	}

}
