package com.hospital.admin.entity;

import java.util.UUID;

import com.hospital.admin.enums.ModulePrivileges;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModulePrivilages  extends BaseEntity{

	@Id
	@GeneratedValue
	private UUID id;

	private UUID subPageId;

	private String privilages;
}
