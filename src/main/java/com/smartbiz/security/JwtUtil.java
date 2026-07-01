package com.smartbiz.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Handles everything JWT-related: creating tokens at login time, and
 * validating/reading them on every subsequent authenticated request.
 *
 * WHY this exists as its own class: token creation and token
 * validation both need the same secret key and the same parsing
 * logic. Centralizing it here means the login endpoints and the
 * request filter (built next) both call into ONE place, rather than
 * duplicating signing/parsing logic in two spots.
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationMs;

    // @Value pulls these straight from application.properties at
    // startup - Spring constructs this bean once, with the secret
    // and expiry already wired in.
    public JwtUtil(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expiration.ms}") long expirationMs) {
        // Keys.hmacShaKeyFor() turns our plain String secret into the
        // SecretKey object the signing algorithm actually needs.
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Builds a signed JWT for a successfully authenticated user.
     *
     * @param username the login username (becomes the JWT's "subject")
     * @param role     "STAFF" or "PATIENT" - embedded so the filter
     *                 can authorize requests without a DB lookup
     * @param userId   the database id of the StaffMember or User row -
     *                 embedded so ownership checks (e.g. "is this
     *                 PATIENT viewing their OWN appointments") can
     *                 compare this against a path variable, without
     *                 looking the user back up by username every time
     */
    public String generateToken(String username, String role, Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Parses and validates a token's signature + expiry in one step.
     * If the token has been tampered with, or is expired, or is
     * malformed, this throws an exception (jjwt's own exception
     * types) - we let that propagate up rather than catching it here,
     * so the calling filter decides how to respond (401, in our case).
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }


    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    /**
     * True if the token's signature is valid AND it hasn't expired.
     * extractAllClaims() already throws on a bad signature or
     * malformed token, so reaching the expiry check at all means the
     * signature was fine - we just additionally confirm it's not
     * past its expiration date.
     */
    public boolean isTokenValid(String token) {
        try {
            Date expiration = extractAllClaims(token).getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            // Covers expired, malformed, or tampered tokens - any of
            // these means "not valid," so we return false rather than
            // letting the exception bubble up here.
            return false;
        }
    }
}