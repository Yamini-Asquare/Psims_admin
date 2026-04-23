package com.hospital.admin.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hospital.admin.response.ModuleRoleResponse;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ← Updated to accept extra info
    public String generateToken(String username, UUID userId, List<ModuleRoleResponse> moduleRoles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("username", username);
        claims.put("moduleRoles", moduleRoles);
        
        // Calculate audience based on modules the user has access to
        List<String> audience = moduleRoles.stream()
                .map(mr -> mr.getModuleName().toLowerCase())
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString()) // sub = userId
                .setIssuer("hospital-admin-service")
                .setAudience(audience.toString()) // Jwts builder aud can be a string or collection depending on version
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract username
    public String extractUsername(String token) {
        return getClaims(token).get("username", String.class);
    }

    // ← Extract userId
    public UUID extractUserId(String token) {
        return UUID.fromString(getClaims(token).get("userId", String.class));
    }

    // ← Extract roleId
    public UUID extractRoleId(String token) {
        return UUID.fromString(getClaims(token).get("roleId", String.class));
    }

    // ← Extract roleName
    public String extractRoleName(String token) {
        return getClaims(token).get("roleName", String.class);
    }

    public UUID extractModuleId(String token) {
        return UUID.fromString(getClaims(token).get("moduleId", String.class));
    }

    public String extractModuleName(String token) {
        return getClaims(token).get("moduleName", String.class);
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
