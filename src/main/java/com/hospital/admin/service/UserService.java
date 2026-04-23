package com.hospital.admin.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hospital.admin.entity.Department;
import com.hospital.admin.entity.Modules;
import com.hospital.admin.entity.Roles;
import com.hospital.admin.entity.UserLogin;
import com.hospital.admin.entity.UserModuleRole;
import com.hospital.admin.entity.Users;
import com.hospital.admin.repository.DepartmentRepository;
import com.hospital.admin.repository.ModulesRepository;
import com.hospital.admin.repository.RolesRepository;
import com.hospital.admin.repository.UserLoginRepository;
import com.hospital.admin.repository.UserModuleRoleRepository;
import com.hospital.admin.repository.UsersRepository;
import com.hospital.admin.request.ForgotPasswordRequest;
import com.hospital.admin.request.ModuleRoleRequest;
import com.hospital.admin.request.UserLoginRequest;
import com.hospital.admin.request.UserRequest;
import com.hospital.admin.response.ApiResponse;
import com.hospital.admin.response.ModuleRoleResponse;
import com.hospital.admin.response.UserLoginResponse;
import com.hospital.admin.response.UserResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	private final UsersRepository usersRepository;
	private final UserLoginRepository userLoginRepository;
	private final PasswordEncoder passwordEncoder;
	private final DepartmentRepository departmentRepository;
	private final RolesRepository rolesRepository;
	private final ModulesRepository modulesRepository;
	private final UserModuleRoleRepository userModuleRoleRepository;

	public UserService(UsersRepository usersRepository, UserLoginRepository userLoginRepository,
			PasswordEncoder passwordEncoder, DepartmentRepository departmentRepository, RolesRepository rolesRepository,
			ModulesRepository modulesRepository, UserModuleRoleRepository userModuleRoleRepository) {
		this.usersRepository = usersRepository;
		this.userLoginRepository = userLoginRepository;
		this.passwordEncoder = passwordEncoder;
		this.departmentRepository = departmentRepository;
		this.rolesRepository = rolesRepository;
		this.modulesRepository = modulesRepository;
		this.userModuleRoleRepository = userModuleRoleRepository;
	}

	// ─── Mappers ─────────────────────────────────────────────────

	private UserResponse toUserResponse(Users user) {

		String departmentName = null;
		if (user.getDepartment() != null) {
			departmentName = departmentRepository.findById(user.getDepartment()).map(Department::getName)
					.orElse("Unknown");
		}

		List<ModuleRoleResponse> moduleRoles = user.getModuleRoles() == null ? List.of()
				: user.getModuleRoles().stream()
						.map(umr -> ModuleRoleResponse.builder().moduleId(umr.getModule().getId())
								.moduleName(umr.getModule().getModuleName()).roleId(umr.getRole().getId())
								.roleName(umr.getRole().getRoleName()).build())
						.collect(Collectors.toList());

		return UserResponse.builder().id(user.getId()).name(user.getName()).empCode(user.getEmpCode())
				.email(user.getEmail()).contact(user.getContact()).moduleRoles(moduleRoles)
				.workLocation(user.getWorkLocation()).department(user.getDepartment()).departmentName(departmentName)
				.isActive(user.getIsActive()).build();
	}

	private UserLoginResponse toLoginResponse(UserLogin login) {
		return UserLoginResponse.builder().id(login.getId()).username(login.getUsername()).email(login.getEmail())
				.userId(login.getUserId()).build();
	}

	// ─── Users CRUD ──────────────────────────────────────────────

	// CREATE
	public ApiResponse<UserResponse> createUser(UserRequest request) {
		try {
			if (usersRepository.findByEmail(request.getEmail()).isPresent()) {
				return ApiResponse.<UserResponse>builder().success(false).message("Email already exists").build();
			}
			if (usersRepository.findByEmpCode(request.getEmpCode()).isPresent()) {
				return ApiResponse.<UserResponse>builder().success(false).message("Employee code already exists")
						.build();
			}

			Users user = Users.builder().name(request.getName()).empCode(request.getEmpCode()).email(request.getEmail())
					.contact(request.getContact()).workLocation(request.getWorkLocation())
					.department(request.getDepartment())
					.isActive(request.getIsActive() != null ? request.getIsActive() : true).build();

			Users savedUser = usersRepository.save(user);

			// Handle assignments
			if (request.getModuleRoles() != null) {
				for (ModuleRoleRequest assignment : request.getModuleRoles()) {
					validateAndAssign(savedUser, assignment);
				}
			}

			return ApiResponse.<UserResponse>builder().success(true).message("User created successfully")
					.data(toUserResponse(savedUser)).build();

		} catch (Exception e) {
			log.error("Error creating user", e);
			return ApiResponse.<UserResponse>builder().success(false).message(e.getMessage()).build();
		}
	}

	private void validateAndAssign(Users user, ModuleRoleRequest request) {
		Roles role = rolesRepository.findById(request.getRoleId())
				.orElseThrow(() -> new RuntimeException("Role not found: " + request.getRoleId()));

		Modules module = modulesRepository.findById(request.getModuleId())
				.orElseThrow(() -> new RuntimeException("Module not found: " + request.getModuleId()));

		// Validate hierarchy: Role -> Module
		if (!role.getModule().getId().equals(module.getId())) {
			throw new RuntimeException(
					"Role " + role.getRoleName() + " does not belong to Module " + module.getModuleName());
		}

		UserModuleRole umr = UserModuleRole.builder().user(user).module(module).role(role).build();
		userModuleRoleRepository.save(umr);
	}

	// GET ALL
	public ApiResponse<List<UserResponse>> getAllUsers() {
		try {
			List<UserResponse> list = usersRepository.findAll().stream().map(this::toUserResponse)
					.collect(Collectors.toList());

			return ApiResponse.<List<UserResponse>>builder().success(true).message("Users fetched successfully")
					.data(list).build();

		} catch (Exception e) {
			log.error("Error fetching users", e);
			return ApiResponse.<List<UserResponse>>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	// GET BY ID
	public ApiResponse<UserResponse> getUserById(UUID id) {
		try {
			Users user = usersRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

			return ApiResponse.<UserResponse>builder().success(true).message("User fetched successfully")
					.data(toUserResponse(user)).build();

		} catch (Exception e) {
			log.error("Error fetching user", e);
			return ApiResponse.<UserResponse>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	// UPDATE
	public ApiResponse<UserResponse> updateUser(UUID id, UserRequest request) {
		try {
			Users existing = usersRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

			// Duplicate email check (exclude self)
			usersRepository.findByEmail(request.getEmail()).filter(u -> !u.getId().equals(id)).ifPresent(u -> {
				throw new RuntimeException("Email already in use");
			});

			// Duplicate empCode check (exclude self)
			usersRepository.findByEmpCode(request.getEmpCode()).filter(u -> !u.getId().equals(id)).ifPresent(u -> {
				throw new RuntimeException("Employee code already in use");
			});

//                        // Validate role exists
//                        rolesRepository.findById(request.getRole())
//                                        .orElseThrow(() -> new RuntimeException(
//                                                        "Role not found with ID: " + request.getRole()));

			// Validate department only if provided
			if (request.getDepartment() != null) {
				departmentRepository.findById(request.getDepartment()).orElseThrow(
						() -> new RuntimeException("Department not found with ID: " + request.getDepartment()));
			}

			existing.setName(request.getName());
			existing.setEmpCode(request.getEmpCode());
			existing.setEmail(request.getEmail());
			existing.setContact(request.getContact());
//                        existing.setRole(request.getRole());
			existing.setWorkLocation(request.getWorkLocation());
			existing.setDepartment(request.getDepartment());
			existing.setIsActive(request.getIsActive() != null ? request.getIsActive() : existing.getIsActive());

			Users updated = usersRepository.save(existing);

			return ApiResponse.<UserResponse>builder().success(true).message("User updated successfully")
					.data(toUserResponse(updated)).build();

		} catch (Exception e) {
			log.error("Error updating user", e);
			return ApiResponse.<UserResponse>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	// DELETE
	public ApiResponse<Void> deleteUser(UUID id) {
	    try {
	        Users user = usersRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

	        // ✅ Soft delete → mark inactive
	        user.setIsActive(false);

	        usersRepository.save(user);

	        return ApiResponse.<Void>builder()
	                .success(true)
	                .message("User deactivated successfully")
	                .data(null)
	                .build();

	    } catch (Exception e) {
	        log.error("Error deactivating user", e);
	        return ApiResponse.<Void>builder()
	                .success(false)
	                .message(e.getMessage())
	                .data(null)
	                .build();
	    }
	}

	// ─── UserLogin CRUD ──────────────────────────────────────────

	// CREATE
	public ApiResponse<UserLoginResponse> createUserLogin(UserLoginRequest request) {
		try {
			// Validate user exists
			usersRepository.findById(request.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

			// Duplicate username check
			if (userLoginRepository.findByUsername(request.getUsername()).isPresent()) {
				return ApiResponse.<UserLoginResponse>builder().success(false).message("Username already exists")
						.data(null).build();
			}

			// Duplicate email check
			if (userLoginRepository.findByEmail(request.getEmail()).isPresent()) {
				return ApiResponse.<UserLoginResponse>builder().success(false).message("Email already exists")
						.data(null).build();
			}

			UserLogin login = UserLogin.builder().username(request.getUsername()).email(request.getEmail())
					.password(passwordEncoder.encode(request.getPassword())).userId(request.getUserId()).build();

			UserLogin saved = userLoginRepository.save(login);

			return ApiResponse.<UserLoginResponse>builder().success(true).message("User login created successfully")
					.data(toLoginResponse(saved)).build();

		} catch (Exception e) {
			log.error("Error creating user login", e);
			return ApiResponse.<UserLoginResponse>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	// GET ALL
	public ApiResponse<List<UserLoginResponse>> getAllUserLogins() {
		try {
			List<UserLoginResponse> list = userLoginRepository.findAll().stream().map(this::toLoginResponse)
					.collect(Collectors.toList());

			return ApiResponse.<List<UserLoginResponse>>builder().success(true)
					.message("User logins fetched successfully").data(list).build();

		} catch (Exception e) {
			log.error("Error fetching user logins", e);
			return ApiResponse.<List<UserLoginResponse>>builder().success(false).message(e.getMessage()).data(null)
					.build();
		}
	}

	// GET BY ID
	public ApiResponse<UserLoginResponse> getUserLoginById(UUID id) {
		try {
			UserLogin login = userLoginRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("User login not found with ID: " + id));

			return ApiResponse.<UserLoginResponse>builder().success(true).message("User login fetched successfully")
					.data(toLoginResponse(login)).build();

		} catch (Exception e) {
			log.error("Error fetching user login", e);
			return ApiResponse.<UserLoginResponse>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	// UPDATE
	public ApiResponse<UserLoginResponse> updateUserLogin(UUID id, UserLoginRequest request) {
		try {
			UserLogin existing = userLoginRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("User login not found with ID: " + id));

			// Duplicate username check (exclude self)
			userLoginRepository.findByUsername(request.getUsername()).filter(u -> !u.getId().equals(id))
					.ifPresent(u -> {
						throw new RuntimeException("Username already in use");
					});

			// Duplicate email check (exclude self)
			userLoginRepository.findByEmail(request.getEmail()).filter(u -> !u.getId().equals(id)).ifPresent(u -> {
				throw new RuntimeException("Email already in use");
			});

			existing.setUsername(request.getUsername());
			existing.setEmail(request.getEmail());

			// Only encode if new password provided
			if (request.getPassword() != null && !request.getPassword().isBlank()) {
				existing.setPassword(passwordEncoder.encode(request.getPassword()));
			}

			UserLogin updated = userLoginRepository.save(existing);

			return ApiResponse.<UserLoginResponse>builder().success(true).message("User login updated successfully")
					.data(toLoginResponse(updated)).build();

		} catch (Exception e) {
			log.error("Error updating user login", e);
			return ApiResponse.<UserLoginResponse>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	// DELETE
	public ApiResponse<Void> deleteUserLogin(UUID id) {
		try {
			userLoginRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("User login not found with ID: " + id));

			userLoginRepository.deleteById(id);

			return ApiResponse.<Void>builder().success(true).message("User login deleted successfully").data(null)
					.build();

		} catch (Exception e) {
			log.error("Error deleting user login", e);
			return ApiResponse.<Void>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	// Forgot Password
	public ApiResponse<UserLoginResponse> forgotPassword(ForgotPasswordRequest request) {
		try {
			// Find directly by userId — no path variable needed
			UserLogin existing = userLoginRepository.findByUserId(request.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

			// Validate username matches the account
			if (!existing.getUsername().equals(request.getUsername())) {
				throw new RuntimeException("Username does not match the account");
			}

			// Encode and update the new password
			existing.setPassword(passwordEncoder.encode(request.getPassword()));

			UserLogin updated = userLoginRepository.save(existing);

			return ApiResponse.<UserLoginResponse>builder().success(true).message("Password reset successfully")
					.data(toLoginResponse(updated)).build();

		} catch (Exception e) {
			log.error("Error resetting password", e);
			return ApiResponse.<UserLoginResponse>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}
}
