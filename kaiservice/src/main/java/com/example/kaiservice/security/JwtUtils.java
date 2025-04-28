package com.example.kaiservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException; // Import SignatureException
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails; // Import UserDetails
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct; // Import PostConstruct
import java.security.Key; // Import Key
import java.util.Date;

@Component // Tandai sebagai Spring component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}") // Ambil secret key dari application.properties
    private String jwtSecret;

    @Value("${jwt.expiration.ms}") // Ambil waktu expired dari application.properties
    private int jwtExpirationMs;

    private Key key; // Key untuk signing JWT

    @PostConstruct // Dipanggil setelah dependency injection selesai
    public void init() {
        // Membuat key yang aman dari secret string
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Method untuk generate token JWT
    public String generateJwtToken(Authentication authentication) {
        // Dapatkan principal (user details) dari object Authentication
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // Set username sebagai subject
                .setIssuedAt(new Date()) // Waktu token dibuat
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Waktu token expired
                .signWith(key, SignatureAlgorithm.HS512) // Tanda tangani dengan key dan algoritma HS512
                .compact(); // Build token menjadi string
    }

    // Method untuk mendapatkan username dari token
    public String getUserNameFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key) // Gunakan key yang sama untuk verifikasi
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
        return claims.getSubject(); // Ambil subject (username)
    }

    // Method untuk memvalidasi token JWT
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true; // Jika tidak ada exception, token valid
        } catch (SignatureException e) { // Spesifik untuk JWT > 0.11.x
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false; // Jika terjadi exception, token tidak valid
    }
}