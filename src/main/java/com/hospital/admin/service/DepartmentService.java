package com.hospital.admin.service;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hospital.admin.entity.Department;
import com.hospital.admin.entity.Modules;
import com.hospital.admin.repository.DepartmentRepository;
import com.hospital.admin.repository.ModulesRepository;
import com.hospital.admin.request.DepartmentRequest;
import com.hospital.admin.response.ApiResponse;
import com.hospital.admin.response.DepartmentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ModulesRepository modulesRepository;

    // Helper: map entity → response DTO
    private DepartmentResponse toResponse(Department dept, Modules module) {
        return DepartmentResponse.builder()
                .id(dept.getId())
                .name(dept.getName())
                .isActive(dept.getIsActive())
                .moduleId(module.getId())
                .moduleName(module.getModuleName())
                .build();
    }

    // CREATE
    public ApiResponse<DepartmentResponse> createDepartment(DepartmentRequest request) {
        try {
            if (request.getModuleId() == null) {
                return ApiResponse.<DepartmentResponse>builder()
                        .success(false)
                        .message("Module ID is required")
                        .data(null)
                        .build();
            }

            Modules module = modulesRepository.findById(request.getModuleId())
                    .orElseThrow(() -> new RuntimeException("Module not found with ID: " + request.getModuleId()));

            Department department = Department.builder()
                    .name(request.getName())
                    .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                    .moduleId(module.getId())
                    .build();

            Department saved = departmentRepository.save(department);

            return ApiResponse.<DepartmentResponse>builder()
                    .success(true)
                    .message("Department created successfully")
                    .data(toResponse(saved, module))
                    .build();

        } catch (Exception e) {
            log.error("Error creating department", e);
            return ApiResponse.<DepartmentResponse>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // GET ALL
    public ApiResponse<List<DepartmentResponse>> getAllDepartments() {
        try {
            List<DepartmentResponse> list = departmentRepository.findAll()
                    .stream()
                    .map(dept -> {
                        Modules module = modulesRepository.findById(dept.getModuleId())
                                .orElseThrow(() -> new RuntimeException("Module not found"));
                        return toResponse(dept, module);
                    })
                    .collect(Collectors.toList());

            return ApiResponse.<List<DepartmentResponse>>builder()
                    .success(true)
                    .message("Departments fetched successfully")
                    .data(list)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching departments", e);
            return ApiResponse.<List<DepartmentResponse>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // GET BY ID
    public ApiResponse<DepartmentResponse> getDepartmentById(UUID id) {
        try {
            Department dept = departmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Department not found with ID: " + id));

            Modules module = modulesRepository.findById(dept.getModuleId())
                    .orElseThrow(() -> new RuntimeException("Module not found"));

            return ApiResponse.<DepartmentResponse>builder()
                    .success(true)
                    .message("Department fetched successfully")
                    .data(toResponse(dept, module))
                    .build();

        } catch (Exception e) {
            log.error("Error fetching department", e);
            return ApiResponse.<DepartmentResponse>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // UPDATE
    public ApiResponse<DepartmentResponse> updateDepartment(UUID id, DepartmentRequest request) {
        try {
            if (request.getModuleId() == null) {
                return ApiResponse.<DepartmentResponse>builder()
                        .success(false)
                        .message("Module ID is required")
                        .data(null)
                        .build();
            }

            Department existing = departmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Department not found with ID: " + id));

            Modules module = modulesRepository.findById(request.getModuleId())
                    .orElseThrow(() -> new RuntimeException("Module not found with ID: " + request.getModuleId()));

            existing.setName(request.getName());
            existing.setIsActive(request.getIsActive() != null ? request.getIsActive() : existing.getIsActive());
            existing.setModuleId(module.getId());

            Department updated = departmentRepository.save(existing);

            return ApiResponse.<DepartmentResponse>builder()
                    .success(true)
                    .message("Department updated successfully")
                    .data(toResponse(updated, module))
                    .build();

        } catch (Exception e) {
            log.error("Error updating department", e);
            return ApiResponse.<DepartmentResponse>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    // DELETE
    public ApiResponse<Void> deleteDepartment(UUID id) {
        try {
            departmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Department not found with ID: " + id));

            departmentRepository.deleteById(id);

            return ApiResponse.<Void>builder()
                    .success(true)
                    .message("Department deleted successfully")
                    .data(null)
                    .build();

        } catch (Exception e) {
            log.error("Error deleting department", e);
            return ApiResponse.<Void>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }
}
