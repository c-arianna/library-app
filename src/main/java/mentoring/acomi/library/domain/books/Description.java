package mentoring.acomi.library.domain.books;

import mentoring.acomi.library.domain.common.ValueObject;

public class Description extends ValueObject<String>{

	public Description(String value)  {
		super(value);
	}

	@Override
	public String validate(String description) {
		return description == null ? "" : description.trim();
	}

}
