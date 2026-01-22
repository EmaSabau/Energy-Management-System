package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entities.AuthUser;
import com.example.demo.entities.Role;
import com.example.demo.repository.AuthUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserSyncPublisher publisher;

    public AuthService(AuthUserRepository authUserRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       UserSyncPublisher publisher) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.publisher = publisher;
    }

    public AuthResponse register(RegisterRequest request) {
        if (authUserRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        UUID userId = UUID.randomUUID();

        AuthUser authUser = new AuthUser();
        authUser.setId(userId);
        authUser.setUsername(request.getUsername());
        authUser.setEmail(request.getEmail());
        authUser.setPassword(passwordEncoder.encode(request.getPassword()));
        authUser.setRole(Set.of(Role.ROLE_CLIENT));

        AuthUser savedUser = authUserRepository.save(authUser);
        publisher.sendUserCreated(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                "ROLE_CLIENT",
                request.getPassword()
        );

        String jwtToken = jwtService.generateToken(savedUser);

        return AuthResponse.builder()
                .token(jwtToken)
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .roles(savedUser.getRole().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList()))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        AuthUser authUser = authUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), authUser.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String jwtToken = jwtService.generateToken(authUser);

        return AuthResponse.builder()
                .token(jwtToken)
                .username(authUser.getUsername())
                .email(authUser.getEmail())
                .roles(authUser.getRole().stream().map(Enum::name).collect(Collectors.toList()))
                .build();
    }

    public boolean validateToken(String token) {
        return jwtService.isTokenValid(token);
    }

    public String getUsernameFromToken(String token) {
        return jwtService.extractUsername(token);
    }

    public Set<String> getRolesFromToken(String token) {
        return jwtService.extractRoles(token);
    }
}