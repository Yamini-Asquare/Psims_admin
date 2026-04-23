package com.hospital.admin.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.admin.entity.DropDownMasters;
import com.hospital.admin.entity.UserLogin;

@Repository
public interface DropDownMastersRepository extends JpaRepository<DropDownMasters, UUID> {

	List<DropDownMasters> findByDropdownType(String dropdownType);

	boolean existsByDropdownTypeAndDropdownValue(String dropdownType, String dropdownValue);
	}
