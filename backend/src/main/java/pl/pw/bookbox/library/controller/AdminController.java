package pl.pw.bookbox.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            b.setTitle(updated.getTitle()); b.setAuthor(updated.getAuthor()); b.setCategory(updated.getCategory()); b.setAvailable(updated.isAvailable());
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
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
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
        return ResponseEntity.ok(loanRepository.findAll());
    }

    @PutMapping("/loans/{id}/return")
    public ResponseEntity<?> markReturned(@RequestHeader(value = "X-ADMIN-KEY", required = false) String key,
                                          @RequestHeader(value = "X-USER-ID", required = false) Long userId,
                                          @PathVariable Long id) {
        if (!authorized(key, userId)) return ResponseEntity.status(403).body("Forbidden");
        return loanRepository.findById(id).map(l -> {
            l.setReturnDate(LocalDate.now());
            l.getBook().setAvailable(true);
            bookRepository.save(l.getBook());
            loanRepository.save(l);
            return ResponseEntity.ok(l);
        }).orElse(ResponseEntity.notFound().build());
    }
}
