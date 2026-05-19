package mentoring.acomi.library.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import mentoring.acomi.library.application.view.LoanView;
import mentoring.acomi.library.infrastructure.persistence.entity.LoanViewEntity;

@Component
public class LoanViewJpaMapper {

	public LoanViewEntity toEntity(LoanView loan) {
		return new LoanViewEntity(loan.id(), loan.isbn(), loan.userId(), loan.start(), loan.end());
	}

	public LoanView toView(LoanViewEntity entity) {

		if (entity == null) {
			return null;
		}

		return new LoanView(entity.getId(),entity.getIsbn(), entity.getUserId(), entity.getStartDate(), entity.getEndDate(), entity.getStatus());
	}
}
