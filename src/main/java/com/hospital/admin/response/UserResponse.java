package com.hospital.admin.response;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String name;
    private String empCode;
    private String email;
    private Long contact;
    private List<ModuleRoleResponse> moduleRoles;
    private String workLocation;
    private UUID department;
    private String departmentName; // resolved from department UUID
    private Boolean isActive;
}
