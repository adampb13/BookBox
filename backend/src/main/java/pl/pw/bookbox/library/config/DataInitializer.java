package pl.pw.bookbox.library.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.pw.bookbox.library.model.Book;
import pl.pw.bookbox.library.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private pl.pw.bookbox.library.repository.UserRepository userRepository;

    @Autowired
    private pl.pw.bookbox.library.service.UserService userService;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Seed sample books up to 200 if DB has fewer than 200 books
        long existing = bookRepository.count();
        if (existing < 200) {
            logger.info("Only {} books found in DB — adding sample books up to 200", existing);
            List<Book> books = new ArrayList<>();

            // keep a few well-known starter books if not already present
            if (existing == 0) {
                Book b1 = new Book();
                b1.setTitle("Clean Code");
                b1.setAuthor("Robert C. Martin");
                b1.setCategory("Programming");
                b1.setAvailable(true);
                books.add(b1);

                Book b2 = new Book();
                b2.setTitle("The Pragmatic Programmer");
                b2.setAuthor("Andrew Hunt");
                b2.setCategory("Programming");
                b2.setAvailable(true);
                books.add(b2);

                Book b3 = new Book();
                b3.setTitle("Design Patterns");
                b3.setAuthor("Erich Gamma");
                b3.setCategory("Programming");
                b3.setAvailable(true);
                books.add(b3);
            }

            // add a larger stack of sample books for testing/search
            String[] categories = new String[]{"Programming","Fiction","Science","History","Philosophy","Art","Biography","Business"};
            for (int i = 4; i <= 200; i++) {
                Book b = new Book();
                b.setTitle("Sample Book " + i);
                b.setAuthor("Author " + ((i % 30) + 1));
                b.setCategory(categories[i % categories.length]);
                // make two-thirds available
                b.setAvailable(i % 3 != 0);
                books.add(b);
            }

            bookRepository.saveAll(books);
            logger.info("Seeded {} books (including generated stack)", books.size());
        }

        // ensure an admin user exists for management UI
        String adminEmail = "admin@bookbox.local";
        var existingAdmin = userRepository.findByEmail(adminEmail);
        if (existingAdmin == null) {
            logger.info("Creating default admin user: {}", adminEmail);
            var admin = userService.registerUser(adminEmail, "admin", "Administrator");
            admin.setAdmin(true);
            userRepository.save(admin);
            logger.info("Admin user created with email '{}' and password 'admin' (change in production)", adminEmail);
        } else if (!existingAdmin.isAdmin()) {
            logger.info("Promoting existing user {} to admin", adminEmail);
            existingAdmin.setAdmin(true);
            userRepository.save(existingAdmin);
        }
    }
}
