package com.hospital.admin.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
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

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ← Updated to accept extra info
    public String generateToken(String username, UUID userId, List<ModuleRoleResponse> moduleRoles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("username", username);
        claims.put("moduleRoles", moduleRoles);
        
        // Extract flat list of roles
        List<String> roles = moduleRoles.stream()
                .map(ModuleRoleResponse::getRoleName)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        claims.put("roles", roles);
        
        boolean isSuperAdmin = roles.contains("SUPERADMIN");

        // Calculate audience based on modules the user has access to
        List<String> audience;
        if (isSuperAdmin) {
            // SUPERADMIN gets access to all services
            audience = java.util.Arrays.asList("bbms", "indent", "hospital", "admin");
        } else {
            audience = moduleRoles.stream()
                .map(mr -> mr.getModuleName().toLowerCase())
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        }

        return Jwts.builder()
                .claims(claims)
                .subject(userId.toString())
                .issuer("hospital-admin-service")
                .audience().add(audience).and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
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
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
