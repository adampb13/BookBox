package pl.pw.bookbox.library.book;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbnIgnoreCase(String isbn);
}
