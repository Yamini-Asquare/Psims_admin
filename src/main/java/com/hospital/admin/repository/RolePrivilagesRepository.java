package com.hospital.admin.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.admin.entity.RolePrivilages;

import jakarta.transaction.Transactional;

@Repository
public interface RolePrivilagesRepository extends JpaRepository<RolePrivilages, UUID>{

	List<RolePrivilages> findByRoleId(UUID roleId);

	boolean existsByRoleIdAndSubPagePrivilageId(UUID roleId, UUID subPagePrivilageId);

	@Transactional
	void deleteByRoleIdAndSubPagePrivilageId(UUID roleId, UUID subPagePrivilageId);
	}
