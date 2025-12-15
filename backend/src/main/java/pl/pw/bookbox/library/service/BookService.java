package pl.pw.bookbox.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pw.bookbox.library.model.Book;
import pl.pw.bookbox.library.repository.BookRepository;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailableTrue();
    }

    public List<Book> searchBooks(String query) {
        return bookRepository.findByAuthorContainingOrTitleContaining(query, query);
    }
}
