package com.hospital.admin.request;

import java.util.UUID;
import lombok.Data;

@Data
public class ModuleRoleRequest {
    private UUID moduleId;
    private UUID roleId;
}

