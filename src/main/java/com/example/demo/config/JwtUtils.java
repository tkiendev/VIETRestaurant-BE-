package com.example.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {
    // Key cố định để token vẫn hợp lệ sau khi restart server.
    // Trong môi trường production nên đọc từ application.properties (jwt.secret).
    private static final String SECRET_BASE64 = "bXlTZWNyZXRLZXlGb3JKV1RBdXRoZW50aWNhdGlvbjIwMjZSZXN0YXVyYW50";
    private final SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_BASE64));
    private final long EXPIRATION_TIME = 86400000; // 1 day

    public String generateToken(String username, String roleName, Integer userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", roleName)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
