package com.gsdeveloper.bookmyslot.controller;

import com.gsdeveloper.bookmyslot.dto.AuthRequest;
import com.gsdeveloper.bookmyslot.dto.AuthResponse;
import com.gsdeveloper.bookmyslot.dto.CustomerRegistrationRequest;
import com.gsdeveloper.bookmyslot.entity.User;
import com.gsdeveloper.bookmyslot.service.UserService;
import jakarta.validation.Valid;
import com.gsdeveloper.bookmyslot.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody CustomerRegistrationRequest request) {
        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPassword(request.password());
        userService.saveUser(user);
        AuthResponse response = authService.login(request.email(), request.password());
        if (response == null) {
            throw new IllegalStateException("Account was created but could not be signed in");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request.getEmail(), request.getPassword());

        if (response != null) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(null, "Invalid Email or Password"));
    }
}
