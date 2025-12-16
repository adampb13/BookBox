package pl.pw.bookbox.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import pl.pw.bookbox.library.model.Book;
import pl.pw.bookbox.library.model.Loan;
import pl.pw.bookbox.library.model.User;
import pl.pw.bookbox.library.repository.BookRepository;
import pl.pw.bookbox.library.repository.LoanRepository;
import pl.pw.bookbox.library.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Value("${admin.key:admin-secret}")
    private String adminKey;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    private boolean authorized(String headerKey, Long userId) {
        if (headerKey != null && headerKey.equals(adminKey)) return true;
        if (userId != null) return userRepository.findById(userId).map(User::isAdmin).orElse(false);
        return false;
    }

    @GetMapping("/books")
    public ResponseEntity<?> allBooks(@RequestHeader(value = "X-ADMIN-KEY", required = false) String key,
                                      @RequestHeader(value = "X-USER-ID", required = false) Long userId) {
        if (!authorized(key, userId)) return ResponseEntity.status(403).body("Forbidden");
        return ResponseEntity.ok(bookRepository.findAll());
    }

    @PostMapping("/books")
    public ResponseEntity<?> createBook(@RequestHeader(value = "X-ADMIN-KEY", required = false) String key,
                                        @RequestHeader(value = "X-USER-ID", required = false) Long userId,
                                        @RequestBody Book b) {
        if (!authorized(key, userId)) return ResponseEntity.status(403).body("Forbidden");
        b.setId(null);
        Book saved = bookRepository.save(b);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<?> updateBook(@RequestHeader(value = "X-ADMIN-KEY", required = false) String key,
                                        @RequestHeader(value = "X-USER-ID", required = false) Long userId,
                                        @PathVariable Long id,
                                        @RequestBody Book updated) {
        if (!authorized(key, userId)) return ResponseEntity.status(403).body("Forbidden");
        return bookRepository.findById(id).map(b -> {
            b.setTitle(updated.getTitle()); b.setAuthor(updated.getAuthor()); b.setCategory(updated.getCategory()); b.setAvailable(updated.isAvailable()); b.setYear(updated.getYear());
            return ResponseEntity.ok(bookRepository.save(b));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@RequestHeader(value = "X-ADMIN-KEY", required = false) String key,
                                        @RequestHeader(value = "X-USER-ID", required = false) Long userId,
                                        @PathVariable Long id) {
        if (!authorized(key, userId)) return ResponseEntity.status(403).body("Forbidden");
        if (!bookRepository.existsById(id)) return ResponseEntity.notFound().build();
        bookRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<?> allUsers(@RequestHeader(value = "X-ADMIN-KEY", required = false) String key,
                                      @RequestHeader(value = "X-USER-ID", required = false) Long userId) {
        if (!authorized(key, userId)) return ResponseEntity.status(403).body("Forbidden");
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader(value = "X-ADMIN-KEY", required = false) String key,
                                        @RequestHeader(value = "X-USER-ID", required = false) Long userId,
                                        @PathVariable Long id) {
        if (!authorized(key, userId)) return ResponseEntity.status(403).body("Forbidden");
        if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();
        // Prevent deletion when user has related loans to avoid FK constraint failures
        if (loanRepository.existsByUserId(id)) {
            return ResponseEntity.status(409).body("Cannot delete user with existing loans");
        }
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (DataIntegrityViolationException ex) {
            // Catch any remaining DB integrity errors and return a friendly response
            return ResponseEntity.status(409).body("Cannot delete user due to related data");
        }
    }
    
    @PostMapping("/users/{id}/promote")
    public ResponseEntity<?> promoteUser(@RequestHeader(value = "X-ADMIN-KEY", required = false) String key,
                                         @RequestHeader(value = "X-USER-ID", required = false) Long userId,
                                         @PathVariable Long id) {
        if (!authorized(key, userId)) return ResponseEntity.status(403).body("Forbidden");
        return userRepository.findById(id).map(u -> {
            u.setAdmin(true);
            userRepository.save(u);
            return ResponseEntity.ok(u);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/loans")
    public ResponseEntity<?> allLoans(@RequestHeader(value = "X-ADMIN-KEY", required = false) String key,
                                      @RequestHeader(value = "X-USER-ID", required = false) Long userId) {
        if (!authorized(key, userId)) return ResponseEntity.status(403).body("Forbidden");
        var loans = loanRepository.findAll().stream().map(l -> {
            String status;
            if (l.getReturnedAt() != null) status = "returned";
            else if (l.getReturnDate() != null && l.getReturnDate().isBefore(LocalDate.now())) status = "overdue";
            else status = "borrowed";
            return new pl.pw.bookbox.library.dto.LoanDto(l.getId(), l.getUser().getId(), l.getBook().getId(), l.getLoanDate(), l.getReturnDate(), l.getReturnedAt(), status);
        }).toList();
        return ResponseEntity.ok(loans);
    }

    @PutMapping("/loans/{id}/return")
    public ResponseEntity<?> markReturned(@RequestHeader(value = "X-ADMIN-KEY", required = false) String key,
                                          @RequestHeader(value = "X-USER-ID", required = false) Long userId,
                                          @PathVariable Long id) {
        if (!authorized(key, userId)) return ResponseEntity.status(403).body("Forbidden");
        return loanRepository.findById(id).map(l -> {
            // mark as returned (record returnedAt separately from due returnDate)
            l.setReturnedAt(LocalDate.now());
            l.getBook().setAvailable(true);
            bookRepository.save(l.getBook());
            loanRepository.save(l);
            return ResponseEntity.ok(l);
        }).orElse(ResponseEntity.notFound().build());
    }
}
