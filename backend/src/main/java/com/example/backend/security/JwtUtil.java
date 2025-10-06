package com.example.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * Utility for JWT creation and validation.
 */
public class JwtUtil {

    private final Key key;
    private final long expirationSeconds;

    public JwtUtil(String secret, long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationSeconds = expirationMinutes * 60;
    }

    // PUBLIC_INTERFACE
    public String generateToken(String username, Map<String, Object> extra) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .addClaims(extra)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key)
                .compact();
    }

    // PUBLIC_INTERFACE
    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
