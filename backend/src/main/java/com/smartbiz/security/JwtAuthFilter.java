package com.smartbiz.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Runs ONCE for every incoming HTTP request (OncePerRequestFilter
 * guarantees that, even if Spring's internals would otherwise dispatch
 * a request through multiple filter chains).
 *
 * WHAT IT DOES: looks for "Authorization: Bearer <token>" on the
 * request. If present and valid, it builds a Spring Security
 * "Authentication" object and stores it in the SecurityContext - this
 * is how the REST of Spring Security (and our @PreAuthorize checks,
 * built next in SecurityConfig) knows WHO is making this request and
 * WHAT role they have, without us manually checking the token in
 * every single Controller method.
 *
 * If there's no token, or it's invalid/expired, we simply don't set
 * an Authentication - we let the request continue down the filter
 * chain anyway (filterChain.doFilter(...) always runs), and it's
 * SecurityConfig's job (next file) to decide whether an unauthenticated
 * request is allowed to reach that particular endpoint or not.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No header, or it doesn't start with "Bearer " - nothing for
        // us to do here. Let the request continue; SecurityConfig
        // will reject it later if the endpoint actually requires
        // authentication.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Strip the "Bearer " prefix (7 characters) to get just the
        // raw token string.
        String token = authHeader.substring(7);

        if (jwtUtil.isTokenValid(token)) {
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            Long userId = jwtUtil.extractUserId(token);

            // Spring Security expects roles prefixed with "ROLE_" when
            // using hasRole(...) checks later (hasAuthority(...) would
            // NOT need the prefix, but hasRole(...) does - this is a
            // Spring Security convention, not something we're
            // inventing). So "STAFF" becomes "ROLE_STAFF" here.
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

            // We pass userId as the "principal" (normally this would
            // be a UserDetails object, but since we don't need
            // anything more complex, the userId itself is enough -
            // anything downstream that needs "who is this" can read
            // it directly off the Authentication object).
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userId, null, List.of(authority));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        // If the token WAS present but invalid/expired, we
        // deliberately do nothing here too - no Authentication gets
        // set, so the request proceeds as anonymous, and
        // SecurityConfig will reject it if the endpoint needs auth.

        filterChain.doFilter(request, response);
    }
}