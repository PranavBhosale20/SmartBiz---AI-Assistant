package com.smartbiz.config;

import com.smartbiz.security.JwtAuthFilter;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // Allow browser preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Authentication
                        .requestMatchers("/api/auth/patient-register").permitAll()
                        .requestMatchers("/api/auth/patient-login").permitAll()
                        .requestMatchers("/api/auth/staff-login").permitAll()
                        .requestMatchers("/api/auth/staff-register").hasRole("STAFF")

                        // Health
                        .requestMatchers("/api/health").permitAll()

                        // Dashboard
                        .requestMatchers("/api/dashboard/**").hasRole("STAFF")

                        // Doctors
                        .requestMatchers(HttpMethod.GET, "/api/doctors/**")
                                .hasAnyRole("STAFF", "PATIENT")
                        .requestMatchers("/api/doctors/**")
                                .hasRole("STAFF")

                        // Visit Types
                        .requestMatchers(HttpMethod.GET, "/api/visit-types/**")
                                .hasAnyRole("STAFF", "PATIENT")
                        .requestMatchers("/api/visit-types/**")
                                .hasRole("STAFF")

                        // Staff only
                        .requestMatchers("/api/products/**").hasRole("STAFF")
                        .requestMatchers("/api/prescriptions/**").hasRole("STAFF")
                        .requestMatchers("/api/bill-items/**").hasRole("STAFF")
                        .requestMatchers("/api/bills/**").hasRole("STAFF")

                        // Staff + Patient
                        .requestMatchers("/api/appointments/**").hasAnyRole("STAFF", "PATIENT")
                        .requestMatchers("/api/users/**").hasAnyRole("STAFF", "PATIENT")
                        .requestMatchers("/api/chat/**").hasAnyRole("STAFF", "PATIENT")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}