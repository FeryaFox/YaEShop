package ru.feryafox.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.*;

public class JwtUtils {
    private final String jwtSecret;
    private final long jwtExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtUtils(String jwtSecret, long jwtExpirationMs, long refreshTokenExpirationMs) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String username, Set<String> userAgents) {
//        List<String> userAgents = List.of(userAgent);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .claim("userAgents", userAgents)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Set<String> getUserAgentsFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Set.copyOf((List<String>) claims.get("userAgents"));
    }

    public String getUsernameFromExpiredToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        } catch (JwtException e) {
            throw e;
        }
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
//            log.debug("Token has expired: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
//            log.debug("Invalid JWT token: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
//            log.debug("Unsupported JWT token: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
//            log.debug("JWT claims string is empty: {}", e.getMessage());
            throw e;
        }
    }

    public String getTokenFromHeader(String authHeader) {
        return authHeader.substring(7);
    }
}
