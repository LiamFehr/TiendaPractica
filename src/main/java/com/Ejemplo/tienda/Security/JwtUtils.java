package com.Ejemplo.tienda.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecretString;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private SecretKey jwtSecret;

    // --- Métodos existentes ---

    private SecretKey getJwtSecret() {
        if (jwtSecret == null) {
            jwtSecret = Keys.hmacShaKeyFor(jwtSecretString.getBytes());
        }
        return jwtSecret;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(getJwtSecret())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    public String generateJwt(String subject) {
        return Jwts.builder()
                   .setSubject(subject)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                   .signWith(getJwtSecret())
                   .compact();
    }

    // --- Nuevo alias para validación simple ---
    /**
     * Alias de isTokenValid, pero sin necesidad de UserDetails.
     * Comprueba firma y expiración.
     */
    public boolean validateToken(String token) {
        try {
            // Si no lanza excepción al parsear, la firma es correcta
            extractAllClaims(token);
            // Y además no esté expirado
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
