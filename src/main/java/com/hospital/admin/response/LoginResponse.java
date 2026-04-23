package com.hospital.admin.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String message;
    private List<ModuleRoleResponse> moduleRoles;
}

