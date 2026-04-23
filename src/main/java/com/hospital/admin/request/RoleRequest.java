package com.hospital.admin.request;

import java.util.UUID;

import com.hospital.admin.entity.Modules;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class RoleRequest {
	private UUID id;

	private String roleName;

	private Boolean isActive;

	private String description;

	private UUID moduleId;
}

