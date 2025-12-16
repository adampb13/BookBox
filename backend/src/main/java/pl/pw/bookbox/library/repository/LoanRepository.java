package pl.pw.bookbox.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pw.bookbox.library.model.Loan;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
