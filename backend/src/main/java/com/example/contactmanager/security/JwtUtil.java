package com.example.contactmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Utility for issuing and validating JWT access and refresh tokens.
 * Access tokens are short-lived (stateless); refresh tokens carry a jti claim
 * that is checked against the database for revocation (stateful).
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-expiration-ms}")
    private long accessExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Value("${app.jwt.issuer}")
    private String issuer;

    private SecretKey key;

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a short-lived access token (stateless, no DB lookup needed).
     *
     * @param userId  the user's database id
     * @param subject the user's email or phone
     * @return the signed access JWT
     */
    public String generateToken(Long userId, String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessExpirationMs);
        return Jwts.builder()
                .issuer(issuer)
                .subject(subject)
                .claim("uid", userId)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * Generates a long-lived refresh token with a unique jti claim for DB-backed revocation.
     *
     * @param userId  the user's database id
     * @param subject the user's email or phone
     * @return the signed refresh JWT
     */
    public String generateRefreshToken(Long userId, String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshExpirationMs);
        return Jwts.builder()
                .issuer(issuer)
                .subject(subject)
                .claim("uid", userId)
                .claim("type", "refresh")
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * Parses and verifies a JWT, returning its claims.
     *
     * @param token the JWT string
     * @return the parsed claims
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks whether a token is syntactically valid and unexpired.
     *
     * @param token the JWT string
     * @return true if the token parses successfully
     */
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Invalid JWT rejected: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Extracts the user id from a JWT.
     *
     * @param token the JWT string
     * @return the user id
     */
    public Long extractUserId(String token) {
        Claims claims = parseClaims(token);
        Object uid = claims.get("uid");
        if (uid instanceof Number number) {
            return number.longValue();
        }
        throw new JwtException("Token is missing user id claim");
    }

    /**
     * Extracts the jti (JWT ID) claim from a refresh token for DB lookup.
     *
     * @param token the refresh JWT string
     * @return the jti claim value
     */
    public String extractJti(String token) {
        Claims claims = parseClaims(token);
        return claims.getId();
    }

    /**
     * Returns the access token expiration in milliseconds.
     *
     * @return access token expiration
     */
    public long getAccessExpirationMs() {
        return accessExpirationMs;
    }

    /**
     * Returns the refresh token expiration in milliseconds.
     *
     * @return refresh token expiration
     */
    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }
}
