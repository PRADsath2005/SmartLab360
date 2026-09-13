package com.smartlab360.backend.config;

import com.smartlab360.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }

    // =========================================
    // PASSWORD ENCODER
    // =========================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // =========================================
    // SECURITY FILTER CHAIN
    // =========================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // =================================
                // CSRF
                // =================================

                .csrf(csrf ->
                        csrf.disable()
                )


                // =================================
                // JWT = STATELESS
                // =================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // =================================
                // AUTHORIZATION
                // =================================

                .authorizeHttpRequests(auth -> auth


                        // =================================
                        // PUBLIC HTML PAGES
                        // =================================

                        .requestMatchers(
                                "/",
                                "/login.html",
                                "/register.html",
                                "/dashboard.html",
                                "/equipment.html",
                                "/equipment-details.html",
                                "/booking.html",
                                "/my-bookings.html",
                                "/admin-bookings.html",
                                "/maintenance.html",
                                "/admin-maintenance.html",
                                "/notifications.html"
                        ).permitAll()


                        // =================================
                        // STATIC FILES
                        // =================================

                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()


                        // =================================
                        // AUTH APIs
                        // =================================

                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()


                        // =================================
                        // EQUIPMENT
                        // =================================

                        // Everyone logged in can view
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/equipment/**"
                        ).authenticated()


                        // Only ADMIN can add
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/equipment/**"
                        ).hasRole("ADMIN")


                        // Only ADMIN can update
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/equipment/**"
                        ).hasRole("ADMIN")


                        // Only ADMIN can delete
                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/equipment/**"
                        ).hasRole("ADMIN")


                        // =================================
                        // BOOKINGS
                        // =================================

                        // Student can view own bookings
                        .requestMatchers(
                                "/api/bookings/user/**"
                        ).authenticated()


                        // Admin can view all bookings
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/bookings"
                        ).hasRole("ADMIN")


                        // Student can create booking
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/bookings"
                        ).authenticated()


                        // Admin can approve/reject
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/bookings/*/status"
                        ).hasRole("ADMIN")


                        // Admin can delete booking
                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/bookings/*"
                        ).hasRole("ADMIN")


                        // Individual booking
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/bookings/*"
                        ).authenticated()


                        // =================================
                        // MAINTENANCE
                        // =================================

                        // Student can view own requests
                        .requestMatchers(
                                "/api/maintenance/user/**"
                        ).authenticated()


                        // Admin can view all requests
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/maintenance"
                        ).hasRole("ADMIN")


                        // Logged-in user can create request
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/maintenance"
                        ).authenticated()


                        // Admin can change maintenance status
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/maintenance/*/status"
                        ).hasRole("ADMIN")


                        // Admin can delete maintenance request
                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/maintenance/*"
                        ).hasRole("ADMIN")


                        // Individual request
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/maintenance/*"
                        ).authenticated()


                        // =================================
                        // NOTIFICATIONS
                        // =================================

                        // User notifications
                        .requestMatchers(
                                "/api/notifications/user/**"
                        ).authenticated()


                        // Mark notification as read
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/notifications/*/read"
                        ).authenticated()


                        // Delete notification
                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/notifications/*"
                        ).authenticated()


                        // =================================
                        // ALL OTHER REQUESTS
                        // =================================

                        .anyRequest().authenticated()

                )


                // =================================
                // DISABLE DEFAULT LOGIN
                // =================================

                .formLogin(form ->
                        form.disable()
                )


                .httpBasic(basic ->
                        basic.disable()
                )


                // =================================
                // JWT FILTER
                // =================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}