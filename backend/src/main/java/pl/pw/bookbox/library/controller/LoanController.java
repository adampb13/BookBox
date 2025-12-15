package pl.pw.bookbox.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pw.bookbox.library.dto.LoanRequestDto;
import pl.pw.bookbox.library.model.Book;
import pl.pw.bookbox.library.model.Loan;
import pl.pw.bookbox.library.model.User;
import pl.pw.bookbox.library.repository.BookRepository;
import pl.pw.bookbox.library.repository.UserRepository;
import pl.pw.bookbox.library.service.LoanService;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

	@Autowired
	private LoanService loanService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BookRepository bookRepository;

	@PostMapping
	public ResponseEntity<?> createLoan(@RequestBody LoanRequestDto dto) {
		User u = userRepository.findById(dto.userId).orElse(null);
		Book b = bookRepository.findById(dto.bookId).orElse(null);
		if (u == null || b == null) {
			return ResponseEntity.badRequest().body("Unknown user or book id");
		}
		try {
			Loan loan = loanService.createLoan(u, b);
			pl.pw.bookbox.library.dto.LoanDto resp = new pl.pw.bookbox.library.dto.LoanDto(
					loan.getId(), loan.getUser().getId(), loan.getBook().getId(), loan.getLoanDate(), loan.getReturnDate());
			return ResponseEntity.ok(resp);
		} catch (IllegalStateException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
	}

	@GetMapping("/user/{userId}")
	public List<pl.pw.bookbox.library.dto.LoanDto> getLoansForUser(@PathVariable Long userId) {
		return loanService.getLoansForUser(userId).stream()
				.map(l -> new pl.pw.bookbox.library.dto.LoanDto(l.getId(), l.getUser().getId(), l.getBook().getId(), l.getLoanDate(), l.getReturnDate()))
				.toList();
	}
}

