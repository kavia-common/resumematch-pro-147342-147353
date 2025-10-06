package com.example.backend.web;

import com.example.backend.model.User;
import com.example.backend.security.JwtUtil;
import com.example.backend.service.UserService;
import com.example.backend.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication endpoints for register and login.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "User registration and login")
public class AuthController {

    private final UserService users;
    private final AuthenticationManager authManager;
    private final JwtUtil jwt;

    public AuthController(UserService users,
                          AuthenticationManager authManager,
                          @Value("${app.security.jwt.secret}") String secret,
                          @Value("${app.security.jwt.expiration-minutes}") long expMinutes) {
        this.users = users;
        this.authManager = authManager;
        this.jwt = new JwtUtil(secret, expMinutes);
    }

    public record RegisterRequest(@Email String email, @NotBlank String password) {}
    public record LoginRequest(@Email String email, @NotBlank String password) {}

    // PUBLIC_INTERFACE
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register", description = "Register a new user using email and password.")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody RegisterRequest req) {
        User existing = users.findByEmail(req.email()).orElse(null);
        if (existing != null) {
            return ApiResponse.fail("Email already registered");
        }
        User u = users.register(req.email(), req.password());
        String token = jwt.generateToken(u.getEmail(), Map.of("uid", u.getId()));
        return ApiResponse.ok(Map.of("token", token, "user", Map.of("id", u.getId(), "email", u.getEmail())));
    }

    // PUBLIC_INTERFACE
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login", description = "Authenticate user and receive JWT.")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        User u = users.findByEmail(req.email()).orElseThrow();
        String token = jwt.generateToken(u.getEmail(), Map.of("uid", u.getId()));
        return ApiResponse.ok(Map.of("token", token, "user", Map.of("id", u.getId(), "email", u.getEmail())));
    }
}
