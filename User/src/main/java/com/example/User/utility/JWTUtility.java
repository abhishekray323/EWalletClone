package com.example.User.utility;

import com.example.User.customExceptions.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;


@Component
public class JWTUtility {

    private final Key key;
    @Getter
    private final long jwtExpirationInMs;

    public JWTUtility( @Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long jwtExpirationInMs){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(String username, Map<String, Object> extraClaims) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .addClaims(extraClaims==null ? Map.of() : extraClaims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String username) {

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            final String sub = claims.getSubject();
            return (sub.equals(username)) && !isTokenExpired(token);
        } catch ( JwtException e) {
            throw new InvalidJwtException("Invalid JWT token", e);
        }

    }

    private boolean isTokenExpired(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Date exp = claims.getExpiration();
        return exp.before(new Date());
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
