package com.collabhub.service;

import com.collabhub.model.User;
import com.collabhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String registerUser(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        if (user.getActive() == null) {
            user.setActive(true);
        }

        if (user.getRole() == null) {
            user.setRole("ROLE_USER");
        }

        userRepository.save(user);
        log.info("New user registered: {}", user.getUsername());

        return "User registered successfully!";
    }

    public User authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("Failed login attempt for username: {}", username);
            return new RuntimeException("Invalid username or password");
        });

        if (!Boolean.TRUE.equals(user.getActive())) {
            log.warn("Login attempt by deactivated user: {}", username);
            throw new RuntimeException("Account is deactivated. Contact an administrator.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Failed login attempt (wrong password) for user: {}", username);
            throw new RuntimeException("Invalid username or password");
        }

        log.info("User logged in successfully: {}", username);
        return user;
    }
}
