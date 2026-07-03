package com.smartbiz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Exposes a single shared BCryptPasswordEncoder bean. BCrypt is a
 * one-way hashing algorithm built specifically for passwords - it's
 * slow ON PURPOSE (to resist brute-force attacks) and automatically
 * handles salting, so two users with the same password get different
 * stored hashes. We NEVER store or compare raw passwords directly -
 * everywhere a password is involved, it goes through this encoder.
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}