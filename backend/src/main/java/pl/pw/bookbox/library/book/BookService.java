package pl.pw.bookbox.library.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pw.bookbox.library.category.Category;
import pl.pw.bookbox.library.category.CategoryRepository;
import org.springframework.util.StringUtils;
import pl.pw.bookbox.library.loan.LoanRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final LoanRepository loanRepository;

    public BookService(BookRepository bookRepository,
                       CategoryRepository categoryRepository,
                    LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.loanRepository =  loanRepository;
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
    public List<Book> advancedSearch(
            String title,
            String author,
            Long categoryId,
            Integer yearFrom,
            Integer yearTo,
            Boolean onlyAvailable,
            String sort
    ) {

        List<Book> all = bookRepository.findAll();
        Stream<Book> stream = all.stream();

        if (StringUtils.hasText(title)) {
            String t = title.toLowerCase(Locale.ROOT);
            stream = stream.filter(b ->
                    b.getTitle() != null && b.getTitle().toLowerCase(Locale.ROOT).contains(t));
        }

        if (StringUtils.hasText(author)) {
            String a = author.toLowerCase(Locale.ROOT);
            stream = stream.filter(b ->
                    b.getAuthor() != null && b.getAuthor().toLowerCase(Locale.ROOT).contains(a));
        }

        if (categoryId != null) {
            stream = stream.filter(b ->
                    b.getCategory() != null && categoryId.equals(b.getCategory().getId()));
        }

        if (yearFrom != null) {
            stream = stream.filter(b ->
                    b.getPublishYear() != null && b.getPublishYear() >= yearFrom);
        }

        if (yearTo != null) {
            stream = stream.filter(b ->
                    b.getPublishYear() != null && b.getPublishYear() <= yearTo);
        }

        if (Boolean.TRUE.equals(onlyAvailable)) {
            // jeśli masz pole availableCopies:
            stream = stream.filter(b ->
                    b.getAvailableCopies() != null && b.getAvailableCopies() > 0);
        }

        List<Book> filtered = stream.collect(Collectors.toList());

        String sortKey = (sort == null) ? "" : sort.toLowerCase(Locale.ROOT);
        Comparator<Book> comparator;

        switch (sortKey) {
            case "popularity" -> comparator = Comparator
                    .comparingLong((Book b) -> loanRepository.countByBookId(b.getId()))
                    .reversed();

            case "dateadded", "date" -> comparator = Comparator
                    .comparing(Book::getCreatedAt,
                            Comparator.nullsLast(Comparator.naturalOrder()))
                    .reversed();

            case "rating" -> comparator = Comparator
                    .comparing(Book::getRating,
                            Comparator.nullsLast(Comparator.naturalOrder()))
                    .reversed();

            default -> comparator = Comparator
                    .comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
        }

        filtered.sort(comparator);
        return filtered;
    }
}
