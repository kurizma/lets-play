package com.jkim.lets_play.auth;

// token generation
// token validation
// token parsing

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("your_256_bit_secret_key__must_be_32byteslong".getBytes());

    
    public String generateToken(String email, String role) {
        // 1 day
        long EXPIRATION = 86400000;
        // long EXPIRATION = 600000  // 10 min
        // long EXPIRATION = 300000  // 5 minutes
        // long EXPIRATION = 60000   // 1 minute
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SECRET_KEY) // new pattern for algo? What?
                .compact();
    }
    
    public  boolean validateToken(String token) {

        try {
            Claims claims = (Claims) Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
        
    }
    
    public String extractEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
    
    public String extractRole(String token) {
        
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("role", String.class);
        
    }
    
}

