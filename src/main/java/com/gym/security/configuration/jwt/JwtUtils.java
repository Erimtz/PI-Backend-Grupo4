package com.gym.security.configuration.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private String secretKey;
    @Value("${jwt.time.expiration}")
    private String timeExpiration;

    @Autowired
    private HttpServletRequest request;

    public String generateAccessToken(String username){

        Instant now = Instant.now();
        Instant expirationTime = now.plusSeconds(86400);
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(expirationTime);

        return Jwts.builder()
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean isTokenValid(String token){
        try {
            Jwts.parser()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token inválido, error: {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token){
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token inválido, error: {}", e.getMessage());
            return null;
        }
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction){
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Key getSignatureKey(){
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getAccessTokenFromRequest() {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            return tokenHeader.substring(7);
        }
        return null;
    }

    public boolean isTokenNearExpiration(String token) {
        try {
            Date expirationDate = Jwts.parser()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            Instant now = Instant.now();
            Date current = Date.from(now);
            long differenceInMillis = expirationDate.getTime() - current.getTime();
            return differenceInMillis < (5 * 60 * 1000);
        } catch (JwtException e) {
            log.error("Error al verificar la expiración del token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isUserAdmin(String token) {
        String role = getClaim(token, claims -> claims.get("role", String.class));
        return role != null && role.equals("ADMIN");
    }

    public String refreshExpiredToken(String expiredToken) {
        if (isTokenNearExpiration(expiredToken)) {
            String username = getUsernameFromToken(expiredToken);
            return generateAccessToken(username);
        }
        return null;
    }
}
