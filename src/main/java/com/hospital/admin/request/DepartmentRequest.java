package com.hospital.admin.request;

import java.util.UUID;

import lombok.Data;

@Data
public class DepartmentRequest {
	private String name;
    private Boolean isActive;
    private UUID moduleId;
}

