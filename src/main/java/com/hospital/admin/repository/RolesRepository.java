package com.hospital.admin.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.admin.entity.Roles;
import com.hospital.admin.entity.UserLogin;

@Repository
public interface RolesRepository extends JpaRepository<Roles, UUID> {


    Optional<Roles> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);
    }
