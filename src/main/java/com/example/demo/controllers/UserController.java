package com.example.demo.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	private PasswordEncoder encoder;

	public void setEncoder(PasswordEncoder encoder) {
		this.encoder = encoder;
	}


	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}

	@PostMapping("/create")
	public ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest) {
		if (createUserRequest.getUsername() == null || createUserRequest.getUsername().isEmpty()) {
			return ResponseEntity.badRequest().body("Username cannot be empty");
		}

		if (createUserRequest.getPassword() == null || createUserRequest.getPassword().length() < 8) {
			return ResponseEntity.badRequest().body("Password must be at least 8 characters long");
		}

		if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			return ResponseEntity.badRequest().body("Passwords do not match");
		}

		if (userRepository.findByUsername(createUserRequest.getUsername()) != null) {
			return ResponseEntity.badRequest().body("Username already exists");
		}

		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		String hashedPassword = encoder.encode(createUserRequest.getPassword());
		user.setPassword(hashedPassword);
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		userRepository.save(user);

		return ResponseEntity.ok(user);
	}

	
}
