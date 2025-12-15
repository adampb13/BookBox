package pl.pw.bookbox.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pw.bookbox.library.model.Book;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAvailableTrue();
    List<Book> findByAuthorContainingOrTitleContaining(String author, String title);
}
