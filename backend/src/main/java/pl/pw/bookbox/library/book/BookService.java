package pl.pw.bookbox.library.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pw.bookbox.library.category.Category;
import pl.pw.bookbox.library.category.CategoryRepository;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository,
                       CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    public Book getByIdOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
    }

    @Transactional
    public Book create(CreateBookRequest request) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));
        }

        Book book = new Book(
                request.getTitle(),
                request.getAuthor(),
                request.getPublishYear(),
                request.getIsbn(),
                category,
                request.getTotalCopies(),
                request.getTotalCopies()  // na start available = total
        );

        return bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found: " + id);
        }
        bookRepository.deleteById(id);
    }
}
