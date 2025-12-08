package pl.pw.bookbox.library.loan;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // GET /api/loans - wszystkie wypożyczenia
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Loan> getAll() {
        return loanService.getAll();
    }

    // GET /api/loans/borrower/{name} - wypożyczenia konkretnej osoby
    @GetMapping("/borrower/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Loan> getByBorrower(@PathVariable String name) {
        return loanService.getByBorrower(name);
    }

    // POST /api/loans - wypożyczenie
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Loan> borrow(@Valid @RequestBody CreateLoanRequest request) {
        try {
            Loan loan = loanService.borrow(request);
            return ResponseEntity.ok(loan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // POST /api/loans/{id}/return - zwrot książki
    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Loan> returnBook(@PathVariable Long id) {
        try {
            Loan loan = loanService.returnBook(id);
            return ResponseEntity.ok(loan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
