package com.smartlab360.backend.service;

import com.smartlab360.backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    /*
     * IMPORTANT:
     * JWT secret should be long and secure.
     */
    private static final String SECRET_KEY =
            "SmartLab360SecretKeyForJWTAuthentication2026VerySecureKey";

    /*
     * Token validity:
     * 24 hours
     */
    private static final long EXPIRATION_TIME =
            1000L * 60 * 60 * 24;

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
    }

    /*
     * Generate JWT token after successful login
     */
    public String generateToken(User user) {

        return Jwts.builder()
                .subject(user.getEmail())

                .claim("userId", user.getId())
                .claim("name", user.getName())
                .claim("role", user.getRole().name())

                .issuedAt(new Date())

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_TIME
                        )
                )

                .signWith(getSigningKey())

                .compact();
    }

    /*
     * Extract email from JWT
     */
    public String extractEmail(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    /*
     * Extract user ID from JWT
     */
    public Long extractUserId(String token) {

        Number userId =
                extractAllClaims(token)
                        .get("userId", Number.class);

        return userId != null
                ? userId.longValue()
                : null;
    }

    /*
     * Extract role from JWT
     */
    public String extractRole(String token) {

        return extractAllClaims(token)
                .get("role", String.class);
    }

    /*
     * Validate token with user email
     */
    public boolean isTokenValid(
            String token,
            String email) {

        try {

            String tokenEmail =
                    extractEmail(token);

            return tokenEmail.equals(email)
                    && !isTokenExpired(token);

        } catch (Exception e) {

            return false;
        }
    }

    /*
     * Check token expiration
     */
    private boolean isTokenExpired(
            String token) {

        Date expiration =
                extractAllClaims(token)
                        .getExpiration();

        return expiration.before(
                new Date()
        );
    }

    /*
     * Extract all JWT claims
     */
    private Claims extractAllClaims(
            String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}