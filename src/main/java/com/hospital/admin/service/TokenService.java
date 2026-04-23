package com.hospital.admin.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hospital.admin.entity.Modules;
import com.hospital.admin.entity.Roles;
import com.hospital.admin.entity.UserLogin;
import com.hospital.admin.entity.UserToken;
import com.hospital.admin.repository.UserTokenRepository;
import com.hospital.admin.response.ModuleRoleResponse;
import com.hospital.admin.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

	private final UserTokenRepository tokenRepository;
	private final JwtUtil jwtUtil; // ← your existing class

	@Value("${jwt.expiration}")
	private long expiration;

	// Call this on login
	public String createAndSaveToken(UserLogin user, UUID userId, List<ModuleRoleResponse> moduleRoles,
			HttpServletRequest request) {

		String jwt = jwtUtil.generateToken(user.getUsername(), userId, moduleRoles);

		UserToken userToken = UserToken.builder().user(user).token(jwt)
				.expiresAt(LocalDateTime.now().plusSeconds(expiration / 1000)).isRevoked(false)
				.deviceInfo(request.getHeader("User-Agent")).ipAddress(request.getRemoteAddr()).build();

		tokenRepository.save(userToken);

		return jwt;
	}

	// Call this on every request (in your JWT filter)
	public boolean isTokenValid(String jwt) {

		// Step 1 — check JWT signature + expiry
		if (!jwtUtil.validateToken(jwt)) {
			return false;
		}

		// Step 2 — check DB (revoked or not)
		return tokenRepository.findByToken(jwt)
				.map(token -> !token.getIsRevoked() && token.getExpiresAt().isAfter(LocalDateTime.now())).orElse(false);
	}

	// Call this on logout
	public void revokeToken(String jwt) {
		tokenRepository.findByToken(jwt).ifPresent(token -> {
			token.setIsRevoked(true);
			tokenRepository.save(token);
		});
	}

	// Call this on logout all devices
	public void revokeAllTokens(UUID userId) {
		List<UserToken> tokens = tokenRepository.findAllByUserIdAndIsRevokedFalse(userId);
		tokens.forEach(token -> token.setIsRevoked(true));
		tokenRepository.saveAll(tokens);
	}
}
