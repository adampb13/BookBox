package pl.pw.bookbox.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pw.bookbox.library.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
