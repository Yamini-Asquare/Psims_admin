package com.hospital.admin.controller;

import com.hospital.admin.request.LoginRequest;
import com.hospital.admin.response.LoginResponse;
import com.hospital.admin.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(
			@RequestBody LoginRequest request,
			HttpServletRequest httpRequest) {

		LoginResponse response = authService.login(request, httpRequest);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(
			@RequestHeader("Authorization") String authHeader) {

		authService.logout(authHeader);
		return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
	}

	@PostMapping("/logout-all")
	public ResponseEntity<?> logoutAll(
			@RequestParam(name = "userId") UUID userId) {
		authService.logoutAll(userId);
		return ResponseEntity.ok(Map.of("message", "Logged out from all devices"));
	}
}
