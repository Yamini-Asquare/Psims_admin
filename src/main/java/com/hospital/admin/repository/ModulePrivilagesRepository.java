package com.hospital.admin.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.admin.entity.ModulePrivilages;

@Repository
public interface ModulePrivilagesRepository extends JpaRepository<ModulePrivilages, UUID> {
	List<ModulePrivilages> findBySubPageId(UUID subPageId);

	boolean existsBySubPageIdAndPrivilages(UUID subPageId, String privilages);

}
