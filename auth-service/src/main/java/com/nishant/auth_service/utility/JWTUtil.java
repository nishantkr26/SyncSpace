package com.nishant.auth_service.utility;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JWTUtil {
  private final Key key;
    private final long expirationTime = 3600000; // 1 hour

    // 1. Inject the secret key string from application.properties
    public JWTUtil(@Value("${jwt.secret}") String secretString) {
        // 2. Convert the plain text string into a secure cryptographic Key instance
        this.key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    
    public String generateToken(String username){
        return Jwts.builder().setSubject(username).setExpiration(new java.util.Date(System.currentTimeMillis() + expirationTime)).signWith(key).compact();
    }

    public Boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public String getUsernameFromToken(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
        
    }
    
}