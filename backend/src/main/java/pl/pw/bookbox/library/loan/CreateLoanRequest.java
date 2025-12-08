package pl.pw.bookbox.library.loan;

import jakarta.validation.constraints.NotNull;

public class CreateLoanRequest {

    @NotNull
    private Long bookId;

    public CreateLoanRequest() {
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}
