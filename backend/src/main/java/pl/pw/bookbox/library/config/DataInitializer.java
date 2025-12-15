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
            List<Book> booksToAdd = new ArrayList<>();

            // ensure a few well-known starter books exist (idempotent)
            if (!bookRepository.existsByTitle("Clean Code")) {
                Book b1 = new Book();
                b1.setTitle("Clean Code");
                b1.setAuthor("Robert C. Martin");
                b1.setCategory("Programming");
                b1.setAvailable(true);
                b1.setYear(2008);
                booksToAdd.add(b1);
            }

            if (!bookRepository.existsByTitle("The Pragmatic Programmer")) {
                Book b2 = new Book();
                b2.setTitle("The Pragmatic Programmer");
                b2.setAuthor("Andrew Hunt");
                b2.setCategory("Programming");
                b2.setAvailable(true);
                b2.setYear(1999);
                booksToAdd.add(b2);
            }

            if (!bookRepository.existsByTitle("Design Patterns")) {
                Book b3 = new Book();
                b3.setTitle("Design Patterns");
                b3.setAuthor("Erich Gamma");
                b3.setCategory("Programming");
                b3.setAvailable(true);
                b3.setYear(1994);
                booksToAdd.add(b3);
            }

            // add a larger stack of sample books for testing/search (skip existing titles)
            String[] categories = new String[]{"Programming","Fiction","Science","History","Philosophy","Art","Biography","Business"};
            for (int i = 4; i <= 200; i++) {
                String title = "Sample Book " + i;
                if (bookRepository.existsByTitle(title)) {
                    continue;
                }
                Book b = new Book();
                b.setTitle(title);
                b.setAuthor("Author " + ((i % 30) + 1));
                b.setCategory(categories[i % categories.length]);
                // make two-thirds available
                b.setAvailable(i % 3 != 0);
                b.setYear(1950 + (i % 75));
                booksToAdd.add(b);
            }

            if (!booksToAdd.isEmpty()) {
                bookRepository.saveAll(booksToAdd);
                logger.info("Seeded {} books (including generated stack)", booksToAdd.size());
            } else {
                logger.info("No new books to seed — existing entries satisfied sample threshold");
            }


        }

        // Backfill missing publication years for existing records (idempotent)
        var missingYears = bookRepository.findByYearIsNull();
        if (!missingYears.isEmpty()) {
            for (var b : missingYears) {
                // prefer known well-known starter books
                if ("Clean Code".equals(b.getTitle())) {
                    b.setYear(2008);
                } else if ("The Pragmatic Programmer".equals(b.getTitle())) {
                    b.setYear(1999);
                } else if ("Design Patterns".equals(b.getTitle())) {
                    b.setYear(1994);
                } else if (b.getTitle() != null && b.getTitle().startsWith("Sample Book ")) {
                    try {
                        int n = Integer.parseInt(b.getTitle().substring("Sample Book ".length()).trim());
                        b.setYear(1950 + (n % 75));
                    } catch (NumberFormatException ignored) {
                        b.setYear(2000);
                    }
                } else {
                    b.setYear(2000);
                }
            }
            bookRepository.saveAll(missingYears);
            logger.info("Backfilled publication_year for {} existing books", missingYears.size());
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
