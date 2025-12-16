package pl.pw.bookbox.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.pw.bookbox.library.dto.BookDto;
import pl.pw.bookbox.library.model.Book;
import pl.pw.bookbox.library.service.BookService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<BookDto> getBooks() {
        // Return all books on the main listing so unavailable books are visible in the UI
        return bookService.getAllBooks().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<BookDto> searchBooks(@RequestParam(required = false) String query,
                                     @RequestParam(required = false) String category,
                                     @RequestParam(required = false) Integer year) {
        return bookService.searchBooks(query, category, year).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private BookDto toDto(Book b) {
        return new BookDto(b.getId(), b.getTitle(), b.getAuthor(), b.getCategory(), b.isAvailable(), b.getYear());
    }
}

