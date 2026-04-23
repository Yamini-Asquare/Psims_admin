package com.hospital.admin.entity;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users extends BaseEntity {
	@Id
	@GeneratedValue
	private UUID id;

	private String name;

	@Column(name = "emp_code")
	private String empCode;

	private Boolean isActive;

	private String email;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<UserModuleRole> moduleRoles;

	private Long contact;

	private String workLocation;

	private UUID department;
}
