package pl.pw.bookbox.library.loan;

import jakarta.persistence.*;
import pl.pw.bookbox.library.book.Book;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // na później podłączymy tu encję użytkownika
    @Column(nullable = false)
    private String borrowerName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(nullable = false)
    private LocalDate loanDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    public Loan() {
    }

    public Loan(String borrowerName,
                Book book,
                LocalDate loanDate,
                LocalDate dueDate,
                LoanStatus status) {
        this.borrowerName = borrowerName;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    // gettery/settery

    public Long getId() {
        return id;
    }

	public void setId(Long id) {
		this.id = id;
	}

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }
}
