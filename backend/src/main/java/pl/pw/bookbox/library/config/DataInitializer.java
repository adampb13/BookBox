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

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Seed sample books if none exist
        if (bookRepository.count() == 0) {
            logger.info("No books found in DB — seeding sample books");
            List<Book> books = new ArrayList<>();

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

            bookRepository.saveAll(books);
            logger.info("Seeded {} books", books.size());
        }
    }
}
