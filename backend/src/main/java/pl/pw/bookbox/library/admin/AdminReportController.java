// backend/src/main/java/pl/pw/bookbox/library/admin/AdminReportController.java
package pl.pw.bookbox.library.admin;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.pw.bookbox.library.book.Book;
import pl.pw.bookbox.library.book.BookRepository;
import pl.pw.bookbox.library.loan.Loan;
import pl.pw.bookbox.library.loan.LoanRepository;
import pl.pw.bookbox.library.user.UserAccountRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final UserAccountRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public AdminReportController(UserAccountRepository userRepository,
                                 BookRepository bookRepository,
                                 LoanRepository loanRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    @GetMapping("/summary")
    public ResponseEntity<AdminSummaryReportResponse> getSummary() {
        long totalUsers = userRepository.count();
        long totalBooks = bookRepository.count();
        long totalLoans = loanRepository.count();
        long activeLoans = loanRepository.countByReturnDateIsNull();
        long overdueLoans = loanRepository.countByReturnDateIsNullAndDueDateBefore(LocalDateTime.now());

        // TOP 5 książek po liczbie wypożyczeń
        List<Loan> allLoans = loanRepository.findAll(); // prosto, bez fancy JPQL
        var grouped = allLoans.stream()
                .collect(Collectors.groupingBy(Loan::getBook, Collectors.counting()));

        List<TopBookDto> topBooks = grouped.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .map(e -> {
                    Book b = e.getKey();
                    return new TopBookDto(
                            b.getId(),
                            b.getTitle(),
                            b.getAuthor(),
                            e.getValue()
                    );
                })
                .collect(Collectors.toList());

        AdminSummaryReportResponse response = new AdminSummaryReportResponse(
                totalUsers,
                totalBooks,
                totalLoans,
                activeLoans,
                overdueLoans,
                topBooks
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/loans")
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanRepository.findAll());
    }
}
