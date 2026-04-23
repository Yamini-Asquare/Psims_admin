package com.hospital.admin.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleResponse {

	private String roleName;
	private UUID roleId;
	private UUID moduleId;
	private String moduleName;
	private Boolean isActive;
	private String description;
}

