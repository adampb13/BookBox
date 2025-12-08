package pl.pw.bookbox.library.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pw.bookbox.library.security.JwtService;
import pl.pw.bookbox.library.user.Role;
import pl.pw.bookbox.library.user.UserAccount;
import pl.pw.bookbox.library.user.UserAccountRepository;

@Service
public class AuthService {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserAccountRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        String hashed = passwordEncoder.encode(request.getPassword());
        UserAccount user = new UserAccount(
                request.getEmail(),
                hashed,
                Role.USER
        );
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserAccount user = (UserAccount) auth.getPrincipal();
        String token = jwtService.generateToken(user);

        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }
}
