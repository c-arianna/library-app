package mentoring.acomi.library.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import mentoring.acomi.library.application.view.UserView;
import mentoring.acomi.library.infrastructure.persistence.entity.UserViewEntity;

@Component
public class UserViewJpaMapper {
	
	public UserViewEntity toEntity(UserView user) {
		return new UserViewEntity(user.id(), user.username());
	}
	
	public UserView toDomain(UserViewEntity entity) {
		return new UserView(entity.getId(), entity.getUsername());	
	}

}
