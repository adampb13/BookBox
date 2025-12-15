package pl.pw.bookbox.library.dto;

import java.time.LocalDate;

public class LoanDto {
    public Long id;
    public Long userId;
    public Long bookId;
    public LocalDate loanDate;
    public LocalDate returnDate;

    public LoanDto() {}

    public LoanDto(Long id, Long userId, Long bookId, LocalDate loanDate, LocalDate returnDate) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }
}
