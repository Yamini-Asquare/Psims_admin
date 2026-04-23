package com.hospital.admin.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.admin.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

}
