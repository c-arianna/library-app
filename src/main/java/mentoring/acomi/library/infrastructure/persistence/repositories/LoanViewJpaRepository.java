package mentoring.acomi.library.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mentoring.acomi.library.domain.model.loans.LoanStatus;
import mentoring.acomi.library.infrastructure.persistence.entity.LoanViewEntity;

@Repository
public interface LoanViewJpaRepository extends JpaRepository<LoanViewEntity, String> {

	@Modifying
	@Query("UPDATE LoanViewEntity l SET l.status = :status, l.updatedAt = CURRENT_TIMESTAMP WHERE l.id = :id")
	int updateStatus(@Param("id") String id, @Param("status") LoanStatus status);

}
