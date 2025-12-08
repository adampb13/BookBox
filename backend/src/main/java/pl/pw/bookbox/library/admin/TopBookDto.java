// backend/src/main/java/pl/pw/bookbox/library/admin/TopBookDto.java
package pl.pw.bookbox.library.admin;

public class TopBookDto {

    private Long bookId;
    private String title;
    private String author;
    private long loanCount;

    public TopBookDto(Long bookId, String title, String author, long loanCount) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.loanCount = loanCount;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public long getLoanCount() {
        return loanCount;
    }
}
