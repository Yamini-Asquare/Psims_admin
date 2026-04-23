package com.hospital.admin.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;

import com.hospital.admin.entity.RolePrivilages;
import com.hospital.admin.request.RolePrivilegeRequest;
import com.hospital.admin.request.RoleRequest;
import com.hospital.admin.response.ApiResponse;
import com.hospital.admin.response.RoleResponse;
import com.hospital.admin.service.RoleOperationsService;

@RestController
@RequestMapping("/api/roles")

public class RoleOperationsController {

	private final RoleOperationsService roleService;

	public RoleOperationsController(RoleOperationsService roleService) {
		this.roleService = roleService;
	}

	// CREATE ROLE
	@PostMapping
	public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest role) {
		return roleService.createRole(role);
	}

	// GET ALL ROLES
	@GetMapping
	public ApiResponse<?> getAllRoles() {
		return roleService.getAllRoles();
	}

	// GET ROLE BY ID
	@GetMapping("/{id}")
	public ApiResponse<RoleResponse> getRoleById(
			@Parameter(required = true) @PathVariable(name = "id") UUID id) {
		return roleService.getRoleById(id);
	}

	// UPDATE ROLE
	@PutMapping("/{id}")
	public ApiResponse<RoleResponse> updateRole(
			@Parameter(required = true) @PathVariable(name = "id") UUID id,
			@RequestBody RoleRequest role) {
		return roleService.updateRole(id, role);
	}

	// DELETE ROLE
	@DeleteMapping("/{id}")
	public ApiResponse<Void> deleteRole(
			@Parameter(required = true) @PathVariable(name = "id") UUID id) {
		return roleService.deleteRole(id);
	}

	// CREATE ROLE PRIVILEGE
	@PostMapping("/privileges")
	public ApiResponse<List<RolePrivilages>> createRolePrivileges(@RequestBody RolePrivilegeRequest dto) {
		return roleService.createRolePrivileges(dto);
	}

	// GET ALL ROLE PRIVILEGES
	@GetMapping("/privileges")
	public ApiResponse<?> getAllRolePrivileges() {
		return roleService.getAllRolePrivileges();
	}

	// GET ROLE PRIVILEGE BY ID
	@GetMapping("/privileges/{id}")
	public ApiResponse<RolePrivilages> getRolePrivilege(
			@Parameter(required = true) @PathVariable(name = "id") UUID id) {
		return roleService.getRolePrivilegeById(id);
	}

	// DELETE ROLE PRIVILEGE
	@DeleteMapping("/privileges")
	public ApiResponse<Void> deleteRolePrivileges(@RequestBody RolePrivilegeRequest dto) {
		return roleService.deleteRolePrivileges(dto);
	}

	@GetMapping("/{roleId}/privileges")
	public ApiResponse<?> getPrivilegesByRoleId(
			@Parameter(required = true) @PathVariable(name = "roleId") UUID roleId) {
		return roleService.getPrivilegesByRoleId(roleId);
	}

}
