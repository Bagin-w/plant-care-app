package com.bagin.plantcare.rest.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtService {

  private final SecretKey key;
  private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24;

  public JwtService(@Value("${jwt.secret}") String secret) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String generateToken(Long userId, String email) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + EXPIRATION_MS);

    return Jwts.builder()
        .subject(email)
        .claim("userId", userId)
        .issuedAt(now)
        .expiration(expiry)
        .signWith(key)
        .compact();
  }

  public Claims extractClaims(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public Long extractUserId(String token) {
    Claims claims = extractClaims(token);
    return claims.get("userId", Long.class);
  }

  public boolean isTokenValid(String token) {
    try {
      extractClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}