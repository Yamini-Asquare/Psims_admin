package com.hospital.admin.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;

import com.hospital.admin.entity.ModulePrivilages;
import com.hospital.admin.entity.ModuleSubPage;
import com.hospital.admin.entity.Modules;
import com.hospital.admin.response.ApiResponse;
import com.hospital.admin.service.ModuleOperationsService;

@RestController
@RequestMapping("/api/modules")
public class ModuleOperationsController {

	private final ModuleOperationsService service;

	public ModuleOperationsController(ModuleOperationsService service) {
		this.service = service;
	}

	// MODULE
	@PostMapping
	public ApiResponse<Modules> createModule(@RequestBody Modules module) {
		return service.createModule(module);
	}

	@GetMapping
	public ApiResponse<?> getModules() {
		return service.getAllModules();
	}

	@GetMapping("/{id}")
	public ApiResponse<Modules> getModule(
			@Parameter(required = true) @PathVariable(name = "id") UUID id) {
		return service.getModule(id);
	}

	@DeleteMapping("/{id}")
	public ApiResponse<Void> deleteModule(
			@Parameter(required = true) @PathVariable(name = "id") UUID id) {
		return service.deleteModule(id);
	}

	// SUB PAGE
	@PostMapping("/subpages")
	public ApiResponse<ModuleSubPage> createSubPage(@RequestBody ModuleSubPage subPage) {
		return service.createSubPage(subPage);
	}

	@GetMapping("/subpages")
	public ApiResponse<?> getSubPages() {
		return service.getAllSubPages();
	}

	// PRIVILEGES
	@PostMapping("/privileges")
	public ApiResponse<ModulePrivilages> createPrivilege(@RequestBody ModulePrivilages privilege) {
		return service.createPrivilege(privilege);
	}

	@GetMapping("/privileges")
	public ApiResponse<?> getPrivileges() {
		return service.getAllPrivileges();
	}

	// GET SUBPAGE BY MODULES
	@GetMapping("/modules/{moduleId}/subpages")
	public ApiResponse<?> getSubPagesByModule(
			@Parameter(required = true) @PathVariable(name = "moduleId") UUID moduleId) {
		return service.getSubPagesByModule(moduleId);
	}

	@GetMapping("/module/{moduleId}/subpagePrivilages")
	public ResponseEntity<ApiResponse<?>> getSubPagesByModuleId(
			@Parameter(required = true) @PathVariable(name = "moduleId") UUID moduleId) {
		return ResponseEntity.ok(service.getSubPagesByModuleId(moduleId));
	}
}
