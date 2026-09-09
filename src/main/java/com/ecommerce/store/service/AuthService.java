package com.ecommerce.store.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.store.dto.AuthResponse;
import com.ecommerce.store.dto.LoginRequest;
import com.ecommerce.store.dto.RegisterRequest;
import com.ecommerce.store.entity.User;
import com.ecommerce.store.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional 
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email '" + request.email() + "' is already registered");
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = User.builder()
                        .email(request.email())
                        .passwordHash(hashedPassword)
                        .firstName(request.firstName())
                        .lastName(request.lastName())
                        .role("ROLE_CUSTOMER")
                        .status("ACTIVE")
                        .build();

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole());

        return AuthResponse.of(token, savedUser.getEmail(), savedUser.getRole());
    }

    @Transactional (readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                                  .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return AuthResponse.of(token, user.getEmail(), user.getRole());
    }
}