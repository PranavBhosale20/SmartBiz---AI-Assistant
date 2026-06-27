package com.smartbiz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// THIS IS TEMPORARY. Right now it tells Spring Security "don't lock
// anything down, let every request through with no login required."
// We're doing this ONLY so we can test our APIs in Postman without
// fighting the default login wall. In Phase 5, we will replace this
// entire class with real rules: STAFF must log in, certain endpoints
// require certain roles, passwords get checked properly, etc.
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // "any request" -> "permit all" -> no authentication needed
            // for ANYTHING right now. This is intentionally wide open -
            // never do this in a real production app.
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            // CSRF protection is mainly relevant for browser-based form
            // submissions with cookies. Since we're building a JSON
            // REST API (tested via Postman, not browser forms), we
            // disable it for now - we'll revisit this properly in
            // Phase 5 when we add real login.
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}