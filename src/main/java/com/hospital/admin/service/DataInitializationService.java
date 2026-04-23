package com.hospital.admin.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hospital.admin.entity.DropDownMasters;
import com.hospital.admin.entity.ModulePrivilages;
import com.hospital.admin.entity.ModuleSubPage;
import com.hospital.admin.entity.Modules;
import com.hospital.admin.entity.RolePrivilages;
import com.hospital.admin.entity.Roles;
import com.hospital.admin.entity.UserLogin;
import com.hospital.admin.entity.Users;
import com.hospital.admin.repository.DropDownMastersRepository;
import com.hospital.admin.repository.ModulePrivilagesRepository;
import com.hospital.admin.repository.ModuleSubPageRepository;
import com.hospital.admin.repository.ModulesRepository;
import com.hospital.admin.repository.RolePrivilagesRepository;
import com.hospital.admin.repository.RolesRepository;
import com.hospital.admin.repository.UserLoginRepository;
import com.hospital.admin.repository.UsersRepository;
import com.hospital.admin.repository.UserModuleRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {

	private final RolesRepository rolesRepository;
	private final UsersRepository usersRepository;
	private final UserLoginRepository userLoginRepository;
	private final ModulesRepository modulesRepository;
	private final ModuleSubPageRepository subpageRepository;
	private final DropDownMastersRepository dropdownRepository;
	private final ModulePrivilagesRepository modulePrivilagesRepository;
	private final RolePrivilagesRepository rolePrivilagesRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserModuleRoleRepository userModuleRoleRepository;

	@Override
	public void run(String... args) {
		createModule();
		createDefaultRoles();
		createDefaultAdminUser();
		createDropdowns();
		createSubPages();
		createSubPagePrivileges();
		createRolePrivileges();
	}

	// STEP 1
	private void createModule() {
		if (!modulesRepository.existsByModuleName("ADMIN")) {
			modulesRepository.save(Modules.builder().moduleName("ADMIN").isActive(true).build());
		}
		if (!modulesRepository.existsByModuleName("BBMS")) {
			modulesRepository.save(Modules.builder().moduleName("BBMS").isActive(true).build());
		}
	}

	// STEP 2
	private void createDefaultRoles() {
		Modules module = modulesRepository.findByModuleName("ADMIN")
				.orElseThrow(() -> new RuntimeException("Module ADMIN not found"));

		if (!rolesRepository.existsByRoleName("SUPERADMIN")) {
			rolesRepository.save(Roles.builder().roleName("SUPERADMIN").description("System Administrator")
					.isActive(true).module(module).build());
		}
	}

	// STEP 3
	private void createDefaultAdminUser() {
		if (userLoginRepository.findByUsername("superadmin").isEmpty()) {
			Roles adminRole = rolesRepository.findByRoleName("SUPERADMIN")
					.orElseThrow(() -> new RuntimeException("SUPERADMIN role not found"));

			Modules module = modulesRepository.findByModuleName("ADMIN")
					.orElseThrow(() -> new RuntimeException("ADMIN module not found"));

			Users user = Users.builder().name("System Admin").empCode("EMP001").email("superadmin@admin.com")
					.isActive(true).contact(9999999999L).workLocation("Head Office").build();

			Users savedUser = usersRepository.save(user);

			// Assign the role through the junction table
			userModuleRoleRepository.save(com.hospital.admin.entity.UserModuleRole.builder().user(savedUser)
					.module(module).role(adminRole).build());

			userLoginRepository.save(UserLogin.builder().username("superadmin").email("admin@admin.com")
					.password(passwordEncoder.encode("superadmin123")).userId(savedUser.getId()).build());
		}
	}

	// STEP 4
	private void createDropdowns() {

		Map<String, List<String>> dropdownData = Map.ofEntries(
				Map.entry("accesstypes", List.of("VIEW", "DELETE", "CREATE", "EDIT", "IMPORT", "EXPORT")),
				Map.entry("gender", List.of("Male", "Female")),
				Map.entry("maritalstatus", List.of("Married", "Unmarried")),
				Map.entry("months", List.of("1", "3", "6", "9", "12"))
		);

		dropdownData.forEach((type, values) -> values.forEach(value -> {
			if (!dropdownRepository.existsByDropdownTypeAndDropdownValue(type, value)) {
				dropdownRepository.save(DropDownMasters.builder().dropdownType(type).dropdownValue(value).build());
			}
		}));
	}

	// STEP 6
	// STEP 6
	private void createSubPages() {
		createAdminSubPages();
		createBBMSSubPages();
	}

	private void createAdminSubPages() {
		Modules module = modulesRepository.findByModuleName("ADMIN")
				.orElseThrow(() -> new RuntimeException("Module ADMIN not found"));

		Map<String, Long> subPageOrderMap = Map.ofEntries(Map.entry("Dashboard", 1L), Map.entry("User Profile", 2L),
				Map.entry("Roles", 3L), Map.entry("Users", 4L), Map.entry("Departments", 5L), Map.entry("Modules", 6L));

		Map<String, Map<String, String>> config = Map.ofEntries(
				Map.entry("Dashboard", Map.of("section", "CORE", "menu", "Dashboard")),
				Map.entry("User Profile", Map.of("section", "OPERATIONS", "menu", "Settings")),
				Map.entry("Roles", Map.of("section", "OPERATIONS", "menu", "Settings")),
				Map.entry("Users", Map.of("section", "OPERATIONS", "menu", "Settings")),
				Map.entry("Departments", Map.of("section", "OPERATIONS", "menu", "Settings")),
				Map.entry("Modules", Map.of("section", "OPERATIONS", "menu", "Settings")));

		initializePages(module, subPageOrderMap, config);
	}

	private void createBBMSSubPages() {
		Modules module = modulesRepository.findByModuleName("BBMS")
				.orElseThrow(() -> new RuntimeException("Module BBMS not found"));

		Map<String, Long> subPageOrderMap = Map.ofEntries(Map.entry("BBMS Dashboard", 1L),
				Map.entry("Donor Registration", 2L), Map.entry("Donor History", 3L), Map.entry("Donor Consent", 4L),
				Map.entry("Medical Assessment", 5L), Map.entry("Questionnaires", 6L), Map.entry("Blood Collection", 7L),
				Map.entry("Camps", 8L), Map.entry("Donor Upload", 9L), Map.entry("Screening", 10L),
				Map.entry("TTI Testing", 11L), Map.entry("Component Management", 12L), Map.entry("QC Microbiology", 13L),
				Map.entry("Patient Registration", 14L), Map.entry("Patient Consent", 15L), Map.entry("Cross Match", 16L),
				Map.entry("Blood Issue", 17L), Map.entry("Transfusion", 18L), Map.entry("Adverse Reactions", 19L),
				Map.entry("Inventory", 20L), Map.entry("Discard Management", 21L), Map.entry("Consumables", 22L),
				Map.entry("Questionnaire Master", 23L));

		Map<String, Map<String, String>> config = Map.ofEntries(
				Map.entry("BBMS Dashboard", Map.of("section", "CORE", "menu", "Dashboard")),
				Map.entry("Donor Registration", Map.of("section", "DONOR MANAGEMENT", "menu", "Registration")),
				Map.entry("Donor History", Map.of("section", "DONOR MANAGEMENT", "menu", "History")),
				Map.entry("Donor Consent", Map.of("section", "DONOR MANAGEMENT", "menu", "Consent")),
				Map.entry("Medical Assessment", Map.of("section", "DONOR MANAGEMENT", "menu", "Assessment")),
				Map.entry("Questionnaires", Map.of("section", "DONOR MANAGEMENT", "menu", "Questionnaires")),
				Map.entry("Blood Collection", Map.of("section", "DONOR MANAGEMENT", "menu", "Collection")),
				Map.entry("Camps", Map.of("section", "DONOR MANAGEMENT", "menu", "Camps")),
				Map.entry("Donor Upload", Map.of("section", "DONOR MANAGEMENT", "menu", "Upload")),
				Map.entry("Screening", Map.of("section", "LAB & TESTING", "menu", "Screening")),
				Map.entry("TTI Testing", Map.of("section", "LAB & TESTING", "menu", "TTI")),
				Map.entry("Component Management", Map.of("section", "LAB & TESTING", "menu", "Components")),
				Map.entry("QC Microbiology", Map.of("section", "LAB & TESTING", "menu", "QC")),
				Map.entry("Patient Registration", Map.of("section", "PATIENT & TRANSFUSION", "menu", "Registration")),
				Map.entry("Patient Consent", Map.of("section", "PATIENT & TRANSFUSION", "menu", "Consent")),
				Map.entry("Cross Match", Map.of("section", "PATIENT & TRANSFUSION", "menu", "Cross Match")),
				Map.entry("Blood Issue", Map.of("section", "PATIENT & TRANSFUSION", "menu", "Issue")),
				Map.entry("Transfusion", Map.of("section", "PATIENT & TRANSFUSION", "menu", "Transfusion")),
				Map.entry("Adverse Reactions", Map.of("section", "PATIENT & TRANSFUSION", "menu", "Reactions")),
				Map.entry("Inventory", Map.of("section", "INVENTORY & QUALITY", "menu", "Inventory")),
				Map.entry("Discard Management", Map.of("section", "INVENTORY & QUALITY", "menu", "Discard")),
				Map.entry("Consumables", Map.of("section", "INVENTORY & QUALITY", "menu", "Consumables")),
				Map.entry("Questionnaire Master", Map.of("section", "INVENTORY & QUALITY", "menu", "Master Data")));

		initializePages(module, subPageOrderMap, config);
	}

	private void initializePages(Modules module, Map<String, Long> subPageOrderMap,
			Map<String, Map<String, String>> config) {

		subPageOrderMap.forEach((name, order) -> {

			Map<String, String> pageConfig = config.getOrDefault(name, Map.of("section", "CORE", "menu", name));
			String section = pageConfig.get("section");
			String menu = pageConfig.get("menu");

			if (!subpageRepository.existsBySubPageName(name)) {

				subpageRepository.save(ModuleSubPage.builder().module(module).subPageName(name).isActive(true)
						.section(section).menuName(menu).subPageOrder(order).build());

			} else {

				ModuleSubPage existing = subpageRepository.findBySubPageName(name).orElse(null);

				if (existing != null) {
					boolean changed = false;
					if (!Objects.equals(existing.getSubPageOrder(), order)) {
						existing.setSubPageOrder(order);
						changed = true;
					}
					if (!Objects.equals(existing.getSection(), section)) {
						existing.setSection(section);
						changed = true;
					}
					if (!Objects.equals(existing.getMenuName(), menu)) {
						existing.setMenuName(menu);
						changed = true;
					}
					if (!Objects.equals(existing.getModule().getId(), module.getId())) {
						existing.setModule(module);
						changed = true;
					}
					if (changed) {
						subpageRepository.save(existing);
					}
				}
			}
		});
	}

	// STEP 7
	private void createSubPagePrivileges() {
		List<ModuleSubPage> subPages = subpageRepository.findAll();
		List<String> accessTypes = List.of("VIEW", "CREATE", "EDIT", "DELETE");

		subPages.forEach(subPage -> accessTypes.forEach(access -> {
			if (!modulePrivilagesRepository.existsBySubPageIdAndPrivilages(subPage.getId(), access)) {
				modulePrivilagesRepository
						.save(ModulePrivilages.builder().subPageId(subPage.getId()).privilages(access).build());
			}
		}));
	}

	// STEP 8
	private void createRolePrivileges() {
		Roles adminRole = rolesRepository.findByRoleName("SUPERADMIN")
				.orElseThrow(() -> new RuntimeException("SUPERADMIN role not found"));

		List<ModulePrivilages> allPrivileges = modulePrivilagesRepository.findAll();

		allPrivileges.forEach(privilege -> {
			if (!rolePrivilagesRepository.existsByRoleIdAndSubPagePrivilageId(adminRole.getId(), privilege.getId())) {
				rolePrivilagesRepository.save(RolePrivilages.builder().roleId(adminRole.getId())
						.subPagePrivilageId(privilege.getId()).build());
			}
		});
	}
}
