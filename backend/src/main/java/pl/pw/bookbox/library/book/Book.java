package pl.pw.bookbox.library.book;

import jakarta.persistence.*;
import pl.pw.bookbox.library.category.Category;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    // na razie autor jako prosty string
    @Column(nullable = false)
    private String author;

    private Integer publishYear;

    private String isbn;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private Integer totalCopies;
    private Integer availableCopies;

    public Book() {
    }

    public Book(String title,
                String author,
                Integer publishYear,
                String isbn,
                Category category,
                Integer totalCopies,
                Integer availableCopies) {

        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.isbn = isbn;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // gettery/settery

    public Long getId() {
        return id;
    }

	public void setId(Long id) {
		this.id = id;
	}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }
}
