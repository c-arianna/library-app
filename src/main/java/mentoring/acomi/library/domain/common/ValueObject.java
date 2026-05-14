package mentoring.acomi.library.domain.common;

import lombok.Getter;

@Getter
public abstract class ValueObject<T> {

	private T value;

	protected ValueObject(T value) {
		this.value = this.validate(value);
	}

	public abstract T validate(T value);
}
