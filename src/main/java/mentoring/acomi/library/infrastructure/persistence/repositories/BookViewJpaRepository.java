package mentoring.acomi.library.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mentoring.acomi.library.infrastructure.persistence.entity.BookViewEntity;

@Repository
public interface BookViewJpaRepository extends JpaRepository<BookViewEntity, String>, JpaSpecificationExecutor<BookViewEntity> {

	@Modifying
	@Query("""
			    UPDATE BookViewEntity b
			    SET b.totalCopies = b.totalCopies + :quantity,
			        b.availableCopies = b.availableCopies + :quantity,
			        b.updatedAt = CURRENT_TIMESTAMP
			    WHERE b.isbn = :isbn
			""")
	void addCopies(@Param("isbn") String isbn, @Param("quantity") int quantity);

}
