package com.smartlab360.backend.controller;

import com.smartlab360.backend.entity.User;
import com.smartlab360.backend.service.JwtService;
import com.smartlab360.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // ============================
    // REGISTER
    // ============================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody User user) {

        try {

            User savedUser =
                    userService.registerUser(user);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            Map.of(
                                    "message",
                                    "Registration successful",

                                    "userId",
                                    savedUser.getId(),

                                    "name",
                                    savedUser.getName(),

                                    "email",
                                    savedUser.getEmail(),

                                    "role",
                                    savedUser.getRole()
                            )
                    );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }

    // ============================
    // LOGIN + JWT
    // ============================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request) {

        String email =
                request.get("email");

        String password =
                request.get("password");

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Email and password are required"
                            )
                    );
        }

        try {

            User user =
                    userService.getUserByEmail(email);

            // Check password
            if (!passwordEncoder.matches(
                    password,
                    user.getPassword())) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(
                                Map.of(
                                        "message",
                                        "Invalid email or password"
                                )
                        );
            }

            // Generate JWT
            String token =
                    jwtService.generateToken(user);

            // Return token + user details
            return ResponseEntity.ok(
                    Map.of(

                            "message",
                            "Login successful",

                            "token",
                            token,

                            "userId",
                            user.getId(),

                            "name",
                            user.getName(),

                            "email",
                            user.getEmail(),

                            "role",
                            user.getRole()
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid email or password"
                            )
                    );
        }
    }
}