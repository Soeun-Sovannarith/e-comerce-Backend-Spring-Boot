package com.e_commerce.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // You can override this in application.properties with a long Base64-encoded secret
    @Value("${jwt.secret:ZmFrZXNlY3JldGtleTEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private int jwtExpiration;

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract any claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parse all claims
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if token expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Generate token for user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }

    // Build JWT token
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Get secure signing key
    private Key getSignKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            if (keyBytes.length < 32) { // HS256 requires 256-bit (32 bytes)
                throw new IllegalArgumentException("JWT secret is too short! Must be >= 256 bits.");
            }
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            // Fallback: generate secure random key if secret invalid
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
    }
}
