package com.hospital.admin.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentResponse {
    private UUID id;
    private String name;
    private Boolean isActive;
    private UUID moduleId;
    private String moduleName;
}
