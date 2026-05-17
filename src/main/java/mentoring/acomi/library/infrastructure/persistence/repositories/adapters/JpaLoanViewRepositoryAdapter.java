package mentoring.acomi.library.infrastructure.persistence.repositories.adapters;

import org.springframework.stereotype.Repository;

import mentoring.acomi.library.application.repositories.LoanViewRepository;
import mentoring.acomi.library.application.view.LoanView;
import mentoring.acomi.library.domain.model.loans.LoanStatus;
import mentoring.acomi.library.infrastructure.persistence.entity.LoanViewEntity;
import mentoring.acomi.library.infrastructure.persistence.mapper.LoanViewJpaMapper;
import mentoring.acomi.library.infrastructure.persistence.repositories.LoanViewJpaRepository;

@Repository
public class JpaLoanViewRepositoryAdapter implements LoanViewRepository {

	private final LoanViewJpaRepository repository;
	private final LoanViewJpaMapper mapper;
	
	public JpaLoanViewRepositoryAdapter(LoanViewJpaRepository repository, LoanViewJpaMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}
	
	@Override
	public void insertRequest(LoanView loan) {
		LoanViewEntity entity = mapper.toEntity(loan);
		repository.save(entity);	
	}

	@Override
	public void updateStatus(String id, LoanStatus status) {
		repository.updateStatus(id, status);
	}

}
