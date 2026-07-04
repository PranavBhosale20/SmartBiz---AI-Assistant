package com.smartbiz.config;

import com.smartbiz.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * CHANGED (Phase 6): replaces the temporary permitAll() configuration.
 * Real rules now: STAFF-only endpoints for clinic management,
 * STAFF-or-PATIENT for appointment/profile endpoints (ownership
 * enforced in the Service layer, not here - see PATIENT ownership
 * note below), and open access only for /api/auth/** (you can't log
 * in if logging in itself requires being logged in).
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                // STATELESS: we never create or rely on an HTTP
                // session - every request must carry its own valid
                // JWT. This is the core difference from classic
                // session-based Spring Security.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Open to everyone - no token required to
                        // register or log in.
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/chat/**").hasAnyRole("STAFF", "PATIENT")
                        .requestMatchers("/api/health").permitAll()
                        
                        .requestMatchers("/api/dashboard/**").hasRole("STAFF")

                        // STAFF-only: clinic management operations.
                        // hasRole("STAFF") checks for the "ROLE_STAFF"
                        // authority our JwtAuthFilter attaches.
                        .requestMatchers("/api/doctors/**").hasRole("STAFF")
                        .requestMatchers("/api/products/**").hasRole("STAFF")
                        .requestMatchers("/api/visit-types/**").hasRole("STAFF")
                        .requestMatchers("/api/prescriptions/**").hasRole("STAFF")
                        .requestMatchers("/api/bill-items/**").hasRole("STAFF")
                        .requestMatchers("/api/bills/**").hasRole("STAFF")

                        // STAFF or PATIENT - both roles can reach
                        // these URLs, but WHICH records they can
                        // actually see/modify is enforced inside the
                        // Service layer (ownership check), not here.
                        // Spring Security's URL matchers can't compare
                        // "the {userId} in this path" against "the
                        // userId embedded in the caller's JWT" - that
                        // comparison needs real Java logic, which is
                        // why it lives in AppointmentService/
                        // UserService instead.
                        .requestMatchers("/api/appointments/**").hasAnyRole("STAFF", "PATIENT")
                        .requestMatchers("/api/users/**").hasAnyRole("STAFF", "PATIENT")

                        // Anything not explicitly listed above
                        // requires SOME valid authentication, but no
                        // specific role - a safe default rather than
                        // accidentally leaving something open.
                        .anyRequest().authenticated()
                )

                // Insert our JWT filter BEFORE Spring Security's own
                // username/password filter - our filter needs to run
                // first so that by the time Spring Security checks
                // "is this request authenticated," our filter has
                // already set the Authentication if a valid token
                // was present.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}