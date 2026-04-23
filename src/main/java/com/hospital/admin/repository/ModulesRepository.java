package com.hospital.admin.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.admin.entity.Modules;
 
@Repository
public interface ModulesRepository extends JpaRepository<Modules, UUID> {

	Optional<Modules> findByModuleName(String moduleName);

	boolean existsByModuleName(String moduleName);
	}
