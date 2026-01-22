package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entities.AuthUser;
import com.example.demo.entities.Role;
import com.example.demo.repository.AuthUserRepository;
import com.example.demo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping
public class AuthUserController {

    private final AuthService authService;
    private final AuthUserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public AuthUserController(AuthService authService, AuthUserRepository repo, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify-jwt")
    public ResponseEntity<Void> verifyJwt(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "token", required = false) String tokenParam) {

        try {
            String token = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else if (tokenParam != null) {
                token = tokenParam;
            }

            if (token != null && authService.validateToken(token)) {
                String username = authService.getUsernameFromToken(token);
                String roles = String.join(",", authService.getRolesFromToken(token));

                return ResponseEntity.ok()
                        .header("X-User-Name", username)
                        .header("X-User-Roles", roles)
                        .build();
            }

            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}
