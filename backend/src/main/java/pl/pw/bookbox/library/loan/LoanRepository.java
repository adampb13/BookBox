package pl.pw.bookbox.library.loan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByBorrowerNameOrderByLoanDateDesc(String borrowerName);
}
