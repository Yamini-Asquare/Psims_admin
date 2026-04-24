package com.hospital.admin.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hospital.admin.entity.ModulePrivilages;
import com.hospital.admin.entity.ModuleSubPage;
import com.hospital.admin.entity.Modules;
import com.hospital.admin.entity.RolePrivilages;
import com.hospital.admin.entity.Roles;
import com.hospital.admin.repository.ModulePrivilagesRepository;
import com.hospital.admin.repository.ModuleSubPageRepository;
import com.hospital.admin.repository.ModulesRepository;
import com.hospital.admin.repository.RolePrivilagesRepository;
import com.hospital.admin.repository.RolesRepository;
import com.hospital.admin.request.RolePrivilegeRequest;
import com.hospital.admin.request.RoleRequest;
import com.hospital.admin.response.ApiResponse;
import com.hospital.admin.response.RoleResponse;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleOperationsService {

	private final RolesRepository rolesRepository;
	private final RolePrivilagesRepository rolePrivilegesRepository;
	private final ModulesRepository modulesRepository;
	private final ModuleSubPageRepository subPageRepository;
	private final ModulePrivilagesRepository modulePrivilegesRepository;

	public RoleOperationsService(RolesRepository rolesRepository, RolePrivilagesRepository rolePrivilegesRepository,
			ModulesRepository modulesRepository, ModuleSubPageRepository subPageRepository,
			ModulePrivilagesRepository modulePrivilegesRepository) {

		this.rolesRepository = rolesRepository;
		this.rolePrivilegesRepository = rolePrivilegesRepository;
		this.modulesRepository = modulesRepository;
		this.subPageRepository = subPageRepository;
		this.modulePrivilegesRepository = modulePrivilegesRepository;
	}

	// CREATE ROLE
	public ApiResponse<RoleResponse> createRole(RoleRequest request) {
		try {
			if (request.getModuleId() == null) {
				return ApiResponse.<RoleResponse>builder().success(false).message("Module ID is required").data(null)
						.build();
			}

			Modules module = modulesRepository.findById(request.getModuleId())
					.orElseThrow(() -> new RuntimeException("Module not found with ID: " + request.getModuleId()));

			Roles role = new Roles();
			role.setRoleName(request.getRoleName());
			role.setDescription(request.getDescription());
			role.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
			role.setModule(module);

			Roles saved = rolesRepository.save(role);

			RoleResponse response = RoleResponse.builder().roleId(saved.getId()).roleName(saved.getRoleName())
					.description(saved.getDescription()).isActive(saved.getIsActive()).moduleId(module.getId())
					.moduleName(module.getModuleName()).build();

			return ApiResponse.<RoleResponse>builder().success(true).message("Role created successfully").data(response)
					.build();

		} catch (Exception e) {
			log.error("Error creating role", e);
			return ApiResponse.<RoleResponse>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	public ApiResponse<RoleResponse> updateRole(UUID id, RoleRequest request) {
		try {
			if (request.getModuleId() == null) {
				return ApiResponse.<RoleResponse>builder().success(false).message("Module ID is required").data(null)
						.build();
			}

			Roles existing = rolesRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));

			Modules module = modulesRepository.findById(request.getModuleId())
					.orElseThrow(() -> new RuntimeException("Module not found with ID: " + request.getModuleId()));

			existing.setRoleName(request.getRoleName());
			existing.setDescription(request.getDescription());
			existing.setIsActive(request.getIsActive() != null ? request.getIsActive() : existing.getIsActive());
			existing.setModule(module);

			Roles updated = rolesRepository.save(existing);

			RoleResponse response = RoleResponse.builder().roleId(updated.getId()).roleName(updated.getRoleName())
					.description(updated.getDescription()).isActive(updated.getIsActive()).moduleId(module.getId())
					.moduleName(module.getModuleName()).build();

			return ApiResponse.<RoleResponse>builder().success(true).message("Role updated successfully").data(response)
					.build();

		} catch (Exception e) {
			log.error("Error updating role", e);
			return ApiResponse.<RoleResponse>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	// GET ALL ROLES
	public ApiResponse<List<RoleResponse>> getAllRoles() {
		try {
			List<Roles> roles = rolesRepository.findAll();

			List<RoleResponse> response = roles.stream()
					.map(role -> RoleResponse.builder().roleId(role.getId()).roleName(role.getRoleName())
							.isActive(role.getIsActive()).description(role.getDescription())
							.moduleId(role.getModule() != null ? role.getModule().getId() : null)
							.moduleName(role.getModule() != null ? role.getModule().getModuleName() : null).build())
					.toList();

			return ApiResponse.<List<RoleResponse>>builder().success(true).message("Roles fetched successfully")
					.data(response).build();

		} catch (Exception e) {
			return ApiResponse.<List<RoleResponse>>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	// GET ROLE BY ID
	public ApiResponse<RoleResponse> getRoleById(UUID id) {
		try {
			Roles role = rolesRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));

			RoleResponse response = RoleResponse.builder().roleId(role.getId()).roleName(role.getRoleName())
					.isActive(role.getIsActive()).description(role.getDescription())
					.moduleId(role.getModule() != null ? role.getModule().getId() : null)
					.moduleName(role.getModule() != null ? role.getModule().getModuleName() : null).build();

			return ApiResponse.<RoleResponse>builder().success(true).message("Role fetched successfully").data(response)
					.build();

		} catch (Exception e) {
			return ApiResponse.<RoleResponse>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	// DELETE ROLE
	public ApiResponse<Void> deleteRole(UUID id) {
		try {
			Roles role = rolesRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

			role.setIsActive(false);
			rolesRepository.save(role);

			return ApiResponse.<Void>builder().success(true).message("Role deactivated successfully").data(null)
					.build();
		} catch (Exception e) {
			return ApiResponse.<Void>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	// CREATE ROLE PRIVILEGE

	public ApiResponse<List<RolePrivilages>> createRolePrivileges(RolePrivilegeRequest dto) {
		try {
			List<RolePrivilages> saved = dto.getSubPagePrivilageIds().stream()
					.map(privId -> RolePrivilages.builder().roleId(dto.getRoleId()).subPagePrivilageId(privId).build())
					.map(rolePrivilegesRepository::save).collect(Collectors.toList());

			return ApiResponse.<List<RolePrivilages>>builder().success(true)
					.message("Role privileges created successfully").data(saved).build();
		} catch (Exception e) {
			return ApiResponse.<List<RolePrivilages>>builder().success(false).message(e.getMessage()).data(null)
					.build();
		}
	}

	// Delete multiple privileges at once
	@Transactional
	public ApiResponse<Void> deleteRolePrivileges(RolePrivilegeRequest dto) {
		try {
			dto.getSubPagePrivilageIds().forEach(
					privId -> rolePrivilegesRepository.deleteByRoleIdAndSubPagePrivilageId(dto.getRoleId(), privId));

			return ApiResponse.<Void>builder().success(true).message("Role privileges deleted successfully").data(null)
					.build();
		} catch (Exception e) {
			return ApiResponse.<Void>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	// GET ALL ROLE PRIVILEGES
	public ApiResponse<List<RolePrivilages>> getAllRolePrivileges() {

		try {

			List<RolePrivilages> list = rolePrivilegesRepository.findAll();

			return ApiResponse.<List<RolePrivilages>>builder().success(true)
					.message("Role privileges fetched successfully").data(list).build();

		} catch (Exception e) {

			return ApiResponse.<List<RolePrivilages>>builder().success(false).message(e.getMessage()).data(null)
					.build();
		}
	}

	// GET ROLE PRIVILEGE BY ID
	public ApiResponse<RolePrivilages> getRolePrivilegeById(UUID id) {

		try {

			RolePrivilages privilege = rolePrivilegesRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Role privilege not found"));

			return ApiResponse.<RolePrivilages>builder().success(true).message("Role privilege fetched successfully")
					.data(privilege).build();

		} catch (Exception e) {

			return ApiResponse.<RolePrivilages>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	// DELETE ROLE PRIVILEGE
	// public ApiResponse<Void> deleteRolePrivilege(UUID id) {
	//
	// try {
	//
	// rolePrivilegesRepository.deleteById(id);
	//
	// return ApiResponse.<Void>builder().success(true).message("Role privilege
	// deleted successfully").data(null)
	// .build();
	//
	// } catch (Exception e) {
	//
	// return
	// ApiResponse.<Void>builder().success(false).message(e.getMessage()).data(null).build();
	// }
	// }

	public ApiResponse<List<Modules>> getModulesByRole(UUID roleId) {

		try {

			List<RolePrivilages> rolePrivileges = rolePrivilegesRepository.findByRoleId(roleId);

			List<UUID> subPageIds = rolePrivileges.stream().map(RolePrivilages::getSubPagePrivilageId).toList();

			List<ModuleSubPage> subPages = subPageRepository.findAllById(subPageIds);

			List<UUID> moduleIds = subPages.stream().map(ModuleSubPage::getModule) // get Modules object
					.map(Modules::getId) // get UUID from Modules
					.distinct().toList();

			List<Modules> modules = modulesRepository.findAllById(moduleIds);

			return ApiResponse.<List<Modules>>builder().success(true).message("Modules fetched successfully")
					.data(modules).build();

		} catch (Exception e) {

			return ApiResponse.<List<Modules>>builder().success(false).message(e.getMessage()).data(null).build();
		}
	}

	// public ApiResponse<?> getPrivilegesByRoleId(UUID roleId) {
	// try {
	// Roles role = rolesRepository.findById(roleId).orElseThrow(() -> new
	// RuntimeException("Role not found"));
	//
	// // Step 1: Get role privileges
	// List<RolePrivilages> rolePrivileges =
	// rolePrivilegesRepository.findByRoleId(roleId);
	// if (rolePrivileges.isEmpty()) {
	// throw new RuntimeException("No privileges found for this role");
	// }
	//
	// // Step 2: Get ModulePrivilages using subPagePrivilageId
	// List<UUID> modulePrivilageIds =
	// rolePrivileges.stream().map(RolePrivilages::getSubPagePrivilageId).toList();
	//
	// List<ModulePrivilages> modulePrivilages =
	// modulePrivilegesRepository.findAllById(modulePrivilageIds);
	//
	// // Step 3: Get unique subPage IDs
	// List<UUID> subPageIds =
	// modulePrivilages.stream().map(ModulePrivilages::getSubPageId).distinct().toList();
	//
	// // Step 4: Fetch subpages
	// List<ModuleSubPage> subPages = subPageRepository.findAllById(subPageIds);
	//
	// // Step 5: Fetch only modules that have matching subpages (not all modules)
	// List<UUID> moduleIds = subPages.stream().map(subPage ->
	// subPage.getModule().getId()) // ← get UUID from
	// // object
	// .distinct().toList();
	//
	// List<Modules> modules = modulesRepository.findAllById(moduleIds); // ← only
	// relevant modules
	//
	// // Step 6: Build response
	// Map<String, Object> response = new HashMap<>();
	// response.put("roleId", role.getId());
	// response.put("roleName", role.getRoleName());
	//
	// List<Map<String, Object>> moduleList = new ArrayList<>();
	//
	// for (Modules module : modules) {
	// List<Map<String, Object>> subPageList = new ArrayList<>();
	//
	// for (ModuleSubPage subPage : subPages) {
	//
	// // ✅ Fixed: compare UUID with UUID
	// if (subPage.getModule().getId().equals(module.getId())) {
	//
	// List<Map<String, Object>> privileges = modulePrivilages.stream()
	// .filter(mp -> mp.getSubPageId().equals(subPage.getId())).map(mp -> {
	// Map<String, Object> privilegeMap = new HashMap<>();
	// privilegeMap.put("privilegeId", mp.getId());
	// privilegeMap.put("privilege", mp.getPrivilages());
	// return privilegeMap;
	// }).toList();
	//
	// Map<String, Object> subPageMap = new HashMap<>();
	// subPageMap.put("subPageId", subPage.getId());
	// subPageMap.put("subPageName", subPage.getSubPageName());
	// subPageMap.put("subPageOrder", subPage.getSubPageOrder());
	// subPageMap.put("privileges", privileges);
	//
	// subPageList.add(subPageMap);
	// }
	// }
	//
	// if (!subPageList.isEmpty()) {
	// Map<String, Object> moduleMap = new HashMap<>();
	// moduleMap.put("moduleId", module.getId());
	// moduleMap.put("moduleName", module.getModuleName());
	// moduleMap.put("subPages", subPageList);
	// moduleList.add(moduleMap);
	// }
	// }
	//
	// response.put("modules", moduleList);
	//
	// return ApiResponse.builder().success(true).message("Privileges fetched
	// successfully").data(response)
	// .build();
	//
	// } catch (Exception e) {
	// return
	// ApiResponse.builder().success(false).message(e.getMessage()).data(null).build();
	// }
	// }
	// }
	public ApiResponse<?> getPrivilegesByRoleId(UUID roleId) {
		try {

			// Step 1: Get role
			Roles role = rolesRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
			UUID roleModuleId = role.getModule().getId();

			// Step 2: Role privileges
			List<RolePrivilages> rolePrivileges = rolePrivilegesRepository.findByRoleId(roleId);

			if (rolePrivileges.isEmpty()) {
				throw new RuntimeException("No privileges found for this role");
			}

			// Step 3: Module privilege IDs
			List<UUID> modulePrivilegeIds = rolePrivileges.stream().map(RolePrivilages::getSubPagePrivilageId).toList();

			List<ModulePrivilages> modulePrivileges = modulePrivilegesRepository.findAllById(modulePrivilegeIds);

			// Step 5: Fetch subPages
			List<UUID> subPageIds = modulePrivileges.stream().map(ModulePrivilages::getSubPageId).distinct().toList();

			List<ModuleSubPage> subPages = subPageRepository.findAllById(subPageIds).stream()
					.filter(sp -> sp.getModule().getId().equals(roleModuleId))
					.toList();
			
			// Filter modulePrivileges to only those that belong to the filtered subPages
			List<UUID> filteredSubPageIds = subPages.stream().map(ModuleSubPage::getId).toList();
			modulePrivileges = modulePrivileges.stream()
					.filter(mp -> filteredSubPageIds.contains(mp.getSubPageId()))
					.toList();

			// Step 4: Group privileges by subPageId
			Map<UUID, List<ModulePrivilages>> privilegeMap = modulePrivileges.stream()
					.collect(Collectors.groupingBy(ModulePrivilages::getSubPageId));

			// ✅ SORT subPages by order
			List<ModuleSubPage> sortedSubPages = new ArrayList<>(subPages);
			sortedSubPages.sort(Comparator.comparing(ModuleSubPage::getSubPageOrder,
					Comparator.nullsLast(Comparator.naturalOrder())));

			// Step 6: Group subPages by module (preserve order)
			Map<UUID, List<ModuleSubPage>> subPageMap = sortedSubPages.stream()
					.collect(Collectors.groupingBy(sp -> sp.getModule().getId(), LinkedHashMap::new, // ✅ preserves
																										// order
							Collectors.toList()));


			// Step 7: Fetch modules
			List<UUID> moduleIds = new ArrayList<>(subPageMap.keySet());
			List<Modules> modules = modulesRepository.findAllById(moduleIds);

			// ✅ OPTIONAL: sort modules (if you add moduleOrder later)
			modules.sort(Comparator.comparing(Modules::getModuleName));

			// Step 8: Build response
			Map<String, Object> response = new LinkedHashMap<>();
			response.put("roleId", role.getId());
			response.put("roleName", role.getRoleName());

			List<Map<String, Object>> moduleList = new ArrayList<>();

			for (Modules module : modules) {

				List<ModuleSubPage> moduleSubPages = subPageMap.getOrDefault(module.getId(), new ArrayList<>());

				// Grouping by section
				Map<String, List<ModuleSubPage>> sectionsMap = moduleSubPages.stream()
						.collect(Collectors.groupingBy(
								sp -> sp.getSection() != null ? sp.getSection() : "OTHERS",
								LinkedHashMap::new,
								Collectors.toList()));

				List<Map<String, Object>> sectionsList = new ArrayList<>();

				for (Map.Entry<String, List<ModuleSubPage>> sectionEntry : sectionsMap.entrySet()) {
					String sectionName = sectionEntry.getKey();
					List<ModuleSubPage> sectionSubPages = sectionEntry.getValue();

					// Grouping by menuName
					Map<String, List<ModuleSubPage>> menusMap = sectionSubPages.stream()
							.collect(Collectors.groupingBy(
									sp -> sp.getMenuName() != null ? sp.getMenuName() : sp.getSubPageName(),
									LinkedHashMap::new,
									Collectors.toList()));

					List<Map<String, Object>> menusList = new ArrayList<>();

					for (Map.Entry<String, List<ModuleSubPage>> menuEntry : menusMap.entrySet()) {
						String menuName = menuEntry.getKey();
						List<ModuleSubPage> menuSubPages = menuEntry.getValue();

						List<Map<String, Object>> subPagesList = new ArrayList<>();

						for (ModuleSubPage subPage : menuSubPages) {
							List<ModulePrivilages> privilegesForSubPage = privilegeMap.getOrDefault(subPage.getId(),
									new ArrayList<>());

							List<Map<String, Object>> privileges = privilegesForSubPage.stream().map(mp -> {
								Map<String, Object> p = new HashMap<>();
								p.put("privilegeId", mp.getId());
								p.put("privilege", mp.getPrivilages());
								return p;
							}).toList();

							Map<String, Object> subPageMapObj = new LinkedHashMap<>();
							subPageMapObj.put("subPageId", subPage.getId());
							subPageMapObj.put("subPageName", subPage.getSubPageName());
							subPageMapObj.put("subPageOrder", subPage.getSubPageOrder());
							subPageMapObj.put("privileges", privileges);

							subPagesList.add(subPageMapObj);
						}

						Map<String, Object> menuMap = new LinkedHashMap<>();
						menuMap.put("menuName", menuName);
						menuMap.put("subPages", subPagesList);
						menusList.add(menuMap);
					}

					Map<String, Object> sectionMap = new LinkedHashMap<>();
					sectionMap.put("sectionName", sectionName);
					sectionMap.put("menus", menusList);
					sectionsList.add(sectionMap);
				}

				if (!sectionsList.isEmpty()) {
					Map<String, Object> moduleMap = new LinkedHashMap<>();
					moduleMap.put("moduleId", module.getId());
					moduleMap.put("moduleName", module.getModuleName());
					moduleMap.put("sections", sectionsList);

					moduleList.add(moduleMap);
				}
			}

			response.put("modules", moduleList);

			return ApiResponse.builder().success(true).message("Privileges fetched successfully").data(response)
					.build();

		} catch (Exception e) {
			return ApiResponse.builder().success(false).message(e.getMessage()).data(null).build();
		}
	}
}
