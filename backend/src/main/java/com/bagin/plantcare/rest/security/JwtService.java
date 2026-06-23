package com.bagin.plantcare.rest.security;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtService {

  // TODO: Nur für local Entwicklung. Später in application.properties auslagern!
  private static final String SECRET = "ein-mindestens-32-zeichen-langer-geheimer-schluessel-hier";
  private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24; // 24 Stunden

  private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

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
