package mentoring.acomi.library.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mentoring.acomi.library.infrastructure.persistence.entity.UserViewEntity;

@Repository
public interface UserViewJpaRepository extends JpaRepository<UserViewEntity, String>{

}
