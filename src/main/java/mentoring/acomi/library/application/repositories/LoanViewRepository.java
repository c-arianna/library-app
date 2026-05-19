package mentoring.acomi.library.application.repositories;

import java.util.Optional;

import mentoring.acomi.library.application.view.LoanView;
import mentoring.acomi.library.domain.model.loans.LoanStatus;

public interface LoanViewRepository {
	 public void insertRequest(LoanView loan);
	 public void updateStatus(String id, LoanStatus status);
	 public Optional<LoanView> findById(String id);
}
