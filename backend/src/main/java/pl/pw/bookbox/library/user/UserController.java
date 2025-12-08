package pl.pw.bookbox.library.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.pw.bookbox.library.loan.Loan;
import pl.pw.bookbox.library.loan.LoanRepository;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserAccountRepository userRepository;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserAccountRepository userRepository,
                          LoanRepository loanRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserAccount getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> getMe() {
        UserAccount user = getCurrentUser();
        UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getCreatedAt()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserAccount user = getCurrentUser();

        // zmiana e-maila tylko jeśli inny
        String newEmail = request.getEmail().trim();
        if (!user.getEmail().equalsIgnoreCase(newEmail)) {
            if (userRepository.existsByEmailIgnoreCase(newEmail)) {
                // możesz zrobić 409 albo 400 – prosto:
                return ResponseEntity.badRequest().build();
            }
            user.setEmail(newEmail);
        }

        user.setFullName(request.getFullName().trim());
        userRepository.save(user);

        UserProfileResponse response = new UserProfileResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getRole().name(),
            user.getCreatedAt()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        UserAccount user = getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/loans")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Loan>> getMyLoans() {
        UserAccount user = getCurrentUser();
        List<Loan> loans = loanRepository
                .findByBorrowerNameIgnoreCaseOrderByLoanDateDesc(user.getEmail());
        return ResponseEntity.ok(loans);
    }
}
