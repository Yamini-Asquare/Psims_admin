package com.hospital.admin.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hospital.admin.entity.Modules;
import com.hospital.admin.entity.Roles;
import com.hospital.admin.entity.UserLogin;
import com.hospital.admin.entity.Users;
import com.hospital.admin.exception.ForbiddenException;
import com.hospital.admin.exception.ResourceNotFoundException;
import com.hospital.admin.repository.RolesRepository;
import com.hospital.admin.repository.UserLoginRepository;
import com.hospital.admin.repository.UsersRepository;
import com.hospital.admin.request.LoginRequest;
import com.hospital.admin.response.LoginResponse;
import com.hospital.admin.response.ModuleRoleResponse;
import com.hospital.admin.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService {

	@Autowired
	private UsersRepository userRepository;

	@Autowired
	private RolesRepository roleRepository;

	private final UserLoginRepository userloginRepository;
	private final JwtUtil jwtUtil;
	private final TokenService tokenService; // ← add this
	private final PasswordEncoder passwordEncoder;

	public AuthService(UserLoginRepository userloginRepository, JwtUtil jwtUtil, TokenService tokenService,
			PasswordEncoder PasswordEncoder) { // ← add
		// this
		this.userloginRepository = userloginRepository;
		this.jwtUtil = jwtUtil;
		this.tokenService = tokenService;
		this.passwordEncoder = PasswordEncoder;
	}

	public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {

		UserLogin userLogin = userloginRepository.findByUsername(request.getUserName())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		if (!passwordEncoder.matches(request.getPassword(), userLogin.getPassword())) {
			throw new BadCredentialsException("Invalid password");
		}

		Users user = userRepository.findById(userLogin.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User data not found"));


		List<ModuleRoleResponse> moduleRoles = user.getModuleRoles().stream()
				.map(umr -> ModuleRoleResponse.builder()
						.moduleId(umr.getModule().getId())
						.moduleName(umr.getModule().getModuleName())
						.roleId(umr.getRole().getId())
						.roleName(umr.getRole().getRoleName())
						.build())
				.collect(Collectors.toList());

		if (moduleRoles.isEmpty()) {
			throw new ForbiddenException("No roles assigned to user");
		}


		// Generate token with multi-role claims and save to DB
		String token = tokenService.createAndSaveToken(userLogin, user.getId(), moduleRoles, httpRequest);

		return LoginResponse.builder()
				.token(token)
				.message("Login successful")
				.moduleRoles(moduleRoles)
				.build();
	}

	// ← Add logout
	public void logout(String authHeader) {
		String jwt = authHeader.substring(7);
		tokenService.revokeToken(jwt);
	}

	// ← Add logout from all devices
	public void logoutAll(UUID userId) {
		tokenService.revokeAllTokens(userId);
	}
}
