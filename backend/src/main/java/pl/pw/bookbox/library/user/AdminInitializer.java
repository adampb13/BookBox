package pl.pw.bookbox.library.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserAccountRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@bookbox.local";
        if (!userRepository.existsByEmailIgnoreCase(adminEmail)) {
            UserAccount admin = new UserAccount(
                adminEmail,
                passwordEncoder.encode("admin123"),
                "System Administrator",
                Role.ADMIN
            );
        userRepository.save(admin);
    }
}

}
