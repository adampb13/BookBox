package pl.pw.bookbox.library.loan;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pw.bookbox.library.book.Book;
import pl.pw.bookbox.library.book.BookRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository,
                       BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Loan borrow(CreateLoanRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found: " + request.getBookId()));

        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No available copies");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String borrowerEmail = auth.getName();

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        LocalDate now = LocalDate.now();
        LocalDate due = now.plusWeeks(3);

        Loan loan = new Loan(
                borrowerEmail,
                book,
                now,
                due,
                LoanStatus.ACTIVE
        );

        return loanRepository.save(loan);
    }
    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new RuntimeException("Loan is not active");
        }

        loan.setReturnDate(LocalDate.now());
        if (loan.getReturnDate().isAfter(loan.getDueDate())) {
            loan.setStatus(LoanStatus.OVERDUE);
        } else {
            loan.setStatus(LoanStatus.RETURNED);
        }

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        return loan;
    }

    public List<Loan> getAll() {
        return loanRepository.findAll();
    }

    public List<Loan> getByBorrower(String borrowerName) {
        return loanRepository.findByBorrowerNameOrderByLoanDateDesc(borrowerName);
    }
}
