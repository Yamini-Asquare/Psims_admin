package com.hospital.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hospital.admin.entity.ModulePrivilages;
import com.hospital.admin.entity.ModuleSubPage;
import com.hospital.admin.entity.Modules;
import com.hospital.admin.repository.ModulePrivilagesRepository;
import com.hospital.admin.repository.ModuleSubPageRepository;
import com.hospital.admin.repository.ModulesRepository;
import com.hospital.admin.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModuleOperationsService {

	private final ModulesRepository modulesRepository;
    private final ModuleSubPageRepository subPageRepository;
    private final ModulePrivilagesRepository privilegeRepository;

    public ModuleOperationsService(
            ModulesRepository modulesRepository,
            ModuleSubPageRepository subPageRepository,
            ModulePrivilagesRepository privilegeRepository) {

        this.modulesRepository = modulesRepository;
        this.subPageRepository = subPageRepository;
        this.privilegeRepository = privilegeRepository;
    }

    // MODULE CREATE
    public ApiResponse<Modules> createModule(Modules module) {

        try {

            Modules saved = modulesRepository.save(module);

            return ApiResponse.<Modules>builder()
                    .success(true)
                    .message("Module created successfully")
                    .data(saved)
                    .build();

        } catch (Exception e) {

            log.error("Error creating module", e);

            return ApiResponse.<Modules>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // MODULE GET ALL
    public ApiResponse<List<Modules>> getAllModules() {

        try {

            List<Modules> list = modulesRepository.findAll();

            return ApiResponse.<List<Modules>>builder()
                    .success(true)
                    .message("Modules fetched successfully")
                    .data(list)
                    .build();

        } catch (Exception e) {

            return ApiResponse.<List<Modules>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // MODULE GET BY ID
    public ApiResponse<Modules> getModule(UUID id) {

        try {

            Modules module = modulesRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Module not found"));

            return ApiResponse.<Modules>builder()
                    .success(true)
                    .message("Module fetched successfully")
                    .data(module)
                    .build();

        } catch (Exception e) {

            return ApiResponse.<Modules>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // MODULE DELETE
    public ApiResponse<Void> deleteModule(UUID id) {

        try {

            modulesRepository.deleteById(id);

            return ApiResponse.<Void>builder()
                    .success(true)
                    .message("Module deleted successfully")
                    .data(null)
                    .build();

        } catch (Exception e) {

            return ApiResponse.<Void>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // SUBPAGE CREATE
    public ApiResponse<ModuleSubPage> createSubPage(ModuleSubPage subPage) {

        try {

            ModuleSubPage saved = subPageRepository.save(subPage);

            return ApiResponse.<ModuleSubPage>builder()
                    .success(true)
                    .message("SubPage created successfully")
                    .data(saved)
                    .build();

        } catch (Exception e) {

            return ApiResponse.<ModuleSubPage>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // SUBPAGE GET ALL
    public ApiResponse<List<ModuleSubPage>> getAllSubPages() {

        try {

            return ApiResponse.<List<ModuleSubPage>>builder()
                    .success(true)
                    .message("SubPages fetched successfully")
                    .data(subPageRepository.findAll())
                    .build();

        } catch (Exception e) {

            return ApiResponse.<List<ModuleSubPage>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // PRIVILEGE CREATE
    public ApiResponse<ModulePrivilages> createPrivilege(ModulePrivilages privilege) {

        try {

            ModulePrivilages saved = privilegeRepository.save(privilege);

            return ApiResponse.<ModulePrivilages>builder()
                    .success(true)
                    .message("Privilege created successfully")
                    .data(saved)
                    .build();

        } catch (Exception e) {

            return ApiResponse.<ModulePrivilages>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // PRIVILEGE GET ALL
    public ApiResponse<List<ModulePrivilages>> getAllPrivileges() {

        try {

            return ApiResponse.<List<ModulePrivilages>>builder()
                    .success(true)
                    .message("Privileges fetched successfully")
                    .data(privilegeRepository.findAll())
                    .build();

        } catch (Exception e) {

            return ApiResponse.<List<ModulePrivilages>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    public ApiResponse<List<ModuleSubPage>> getSubPagesByModule(UUID moduleId) {

        try {

            List<ModuleSubPage> pages = subPageRepository.findByModuleId(moduleId);

            return ApiResponse.<List<ModuleSubPage>>builder()
                    .success(true)
                    .message("Subpages fetched successfully")
                    .data(pages)
                    .build();

        } catch (Exception e) {

            return ApiResponse.<List<ModuleSubPage>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

   public ApiResponse<?> getSubPagesByModuleId(UUID moduleId) {
    try {

        Modules module = modulesRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        // ✅ ORDERED FETCH
        List<ModuleSubPage> subPages =
                subPageRepository.findByModuleIdOrderBySubPageOrderAsc(moduleId);

        if (subPages.isEmpty()) {
            throw new RuntimeException("No subpages found for this module");
        }

        List<Map<String, Object>> subPageList = new ArrayList<>();

        for (ModuleSubPage subPage : subPages) {

            List<Map<String, Object>> privileges = privilegeRepository
                    .findBySubPageId(subPage.getId())
                    .stream()
                    .map(mp -> {
                        Map<String, Object> privilegeMap = new HashMap<>();
                        privilegeMap.put("privilegeId", mp.getId());
                        privilegeMap.put("privilege", mp.getPrivilages());
                        return privilegeMap;
                    })
                    .toList();

            Map<String, Object> subPageMap = new HashMap<>();
            subPageMap.put("subPageId", subPage.getId());
            subPageMap.put("subPageName", subPage.getSubPageName());
            subPageMap.put("subPageOrder", subPage.getSubPageOrder());
            subPageMap.put("isActive", subPage.getIsActive());
            subPageMap.put("privileges", privileges);

            subPageList.add(subPageMap);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("moduleId", module.getId());
        response.put("moduleName", module.getModuleName());
        response.put("subPages", subPageList);

        return ApiResponse.builder()
                .success(true)
                .message("SubPages fetched successfully")
                .data(response)
                .build();

    } catch (Exception e) {
        return ApiResponse.builder()
                .success(false)
                .message(e.getMessage())
                .data(null)
                .build();
    }
}
}
