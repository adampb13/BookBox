package pl.pw.bookbox.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pw.bookbox.library.dto.UserRegistrationDto;
import pl.pw.bookbox.library.model.User;
import pl.pw.bookbox.library.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody UserRegistrationDto dto) {
		if (userService.getUserByEmail(dto.email) != null) {
			return ResponseEntity.badRequest().body("Email already used");
		}
		User u = userService.registerUser(dto.email, dto.password, dto.name);
		return ResponseEntity.ok(u);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserRegistrationDto dto) {
		User u = userService.getUserByEmail(dto.email);
		if (u == null || !userService.checkPassword(u, dto.password)) {
			return ResponseEntity.status(401).body("Invalid credentials");
		}
		// For now return user object as simple proof of auth
		return ResponseEntity.ok(u);
	}
}
