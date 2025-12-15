package pl.pw.bookbox.library.dto;

public class BookDto {
    public Long id;
    public String title;
    public String author;
    public String category;
    public boolean available;

    public BookDto() {}

    public BookDto(Long id, String title, String author, String category, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.available = available;
    }
}
