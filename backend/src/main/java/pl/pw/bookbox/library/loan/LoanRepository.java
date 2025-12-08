package pl.pw.bookbox.library.loan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByBorrowerNameOrderByLoanDateDesc(String borrowerName);
    List<Loan> findByBorrowerNameIgnoreCaseOrderByLoanDateDesc(String borrowerName);

    long countByBookId(Long bookId);  // do popularności

    long countByReturnDateIsNull();   // aktywne wypożyczenia

    long countByReturnDateIsNullAndDueDateBefore(LocalDateTime dateTime);
}
