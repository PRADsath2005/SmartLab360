package com.smartlab360.backend.service;

import com.smartlab360.backend.entity.Role;
import com.smartlab360.backend.entity.User;
import com.smartlab360.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================
    // REGISTER USER
    // ============================

    public User registerUser(User user) {

        // Check whether email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Public registration can create STUDENT accounts only
        user.setRole(Role.STUDENT);

        // Encrypt password before saving
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(user);
    }

    // ============================
    // GET ALL USERS
    // ============================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ============================
    // GET USER BY EMAIL
    // ============================

    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );
    }
}