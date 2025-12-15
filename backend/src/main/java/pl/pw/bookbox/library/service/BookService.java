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

    public List<Book> searchBooks(String query, String category, Integer year) {
        List<Book> base;
        if (query == null || query.isBlank()) {
            base = bookRepository.findAll();
        } else {
            base = bookRepository.findByAuthorContainingOrTitleContainingOrCategoryContaining(query, query, query);
        }
        return base.stream()
                .filter(b -> category == null || category.isBlank() || (b.getCategory() != null && b.getCategory().equalsIgnoreCase(category)))
                .filter(b -> year == null || (b.getYear() != null && b.getYear().equals(year)))
                .toList();
    }
}
