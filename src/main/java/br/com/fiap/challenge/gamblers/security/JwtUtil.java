package br.com.fiap.challenge.gamblers.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${app.jwt.secret:changeitmustbeverylong}")
    private String secret;

    @Value("${app.jwt.expiration-ms:3600000}")
    private long expirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        try {
            this.key = Keys.hmacShaKeyFor(secret.getBytes());
        } catch (WeakKeyException ex) {
            // Fallback: generate a secure key to allow the application to start locally.
            log.warn("Configured JWT secret is too short for HS256 ({} bits). Generating a secure random key for runtime. " +
                    "PRODUCTION: please set 'app.jwt.secret' to a secure 32+ byte value.", ex.getMessage());
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
    }

    public String generateToken(String subject) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
