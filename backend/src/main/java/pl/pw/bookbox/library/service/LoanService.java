package pl.pw.bookbox.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pw.bookbox.library.model.Book;
import pl.pw.bookbox.library.model.Loan;
import pl.pw.bookbox.library.model.User;
import pl.pw.bookbox.library.repository.LoanRepository;

import java.time.LocalDate;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private pl.pw.bookbox.library.repository.BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    public Loan createLoan(User user, Book book) {
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is not available for loan");
        }
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(LocalDate.now().plusWeeks(2)); // Default 2-week loan
        book.setAvailable(false); // Mark book as unavailable
        bookRepository.save(book);
        return loanRepository.save(loan);
    }

    public java.util.List<Loan> getLoansForUser(Long userId) {
        return loanRepository.findByUserId(userId);
    }
}
