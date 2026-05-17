package mentoring.acomi.library.application.repositories;

import java.util.Optional;

import mentoring.acomi.library.application.view.UserView;

public interface UserViewRepository {
	public void add(UserView user);
	public Optional<UserView> findById(String id);
}
