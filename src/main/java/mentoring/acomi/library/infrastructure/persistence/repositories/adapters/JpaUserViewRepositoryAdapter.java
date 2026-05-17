package mentoring.acomi.library.infrastructure.persistence.repositories.adapters;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import mentoring.acomi.library.application.repositories.UserViewRepository;
import mentoring.acomi.library.application.view.UserView;
import mentoring.acomi.library.infrastructure.persistence.entity.UserViewEntity;
import mentoring.acomi.library.infrastructure.persistence.mapper.UserViewJpaMapper;
import mentoring.acomi.library.infrastructure.persistence.repositories.UserViewJpaRepository;

@Repository
public class JpaUserViewRepositoryAdapter implements UserViewRepository {

	private final UserViewJpaRepository repository;
	private final UserViewJpaMapper mapper;
	
	public JpaUserViewRepositoryAdapter(UserViewJpaRepository repository, UserViewJpaMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;	
	}
	
	@Override
	public void add(UserView user) {
		UserViewEntity entity = mapper.toEntity(user);
		repository.save(entity);
	}

	@Override
	public Optional<UserView> findById(String id) {
		Optional<UserViewEntity> entity = repository.findById(id);
		return entity.isEmpty() ? Optional.empty() : Optional.of(mapper.toDomain(entity.get()));
	}

}
