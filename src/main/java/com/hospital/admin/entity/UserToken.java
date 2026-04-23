package com.hospital.admin.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserToken extends BaseEntity {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserLogin user;

	@Column(columnDefinition = "TEXT", nullable = false) // ← fix here
	private String token;

	private LocalDateTime expiresAt;

	private LocalDateTime lastUsedAt;

	private Boolean isRevoked;

	@Column(columnDefinition = "TEXT") // ← device info can also be long
	private String deviceInfo;

	private String ipAddress;
}
