package mentoring.acomi.library.domain.model.users;

import org.springframework.util.ObjectUtils;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import mentoring.acomi.library.domain.users.errors.InvalidUser;

@EqualsAndHashCode
@ToString
@Builder
@Getter
public class User {

	private String id;
	private String username;

	public User(String id, String username) {
		this.id = id;
		this.username = username;
	}

	public static User from(String id, String username) {

		if (username == null) {
			throw InvalidUser.empty();
		}

		username = username.trim();

		if (ObjectUtils.isEmpty(username)) {
			throw InvalidUser.empty();
		}

		if (username.length() < 3) {
			throw InvalidUser.tooShort();
		}

		if (username.length() > 100) {
			throw InvalidUser.tooLong();
		}

		return new User(id, username);

	}

}
