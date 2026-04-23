package com.hospital.admin.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModuleRoleResponse {
    private UUID moduleId;
    private String moduleName;
    private UUID roleId;
    private String roleName;
}

