package com.hospital.admin.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hospital.admin.entity.UserModuleRole;

@Repository
public interface UserModuleRoleRepository extends JpaRepository<UserModuleRole, UUID> {
    List<UserModuleRole> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
    boolean existsByUserAndModuleAndRole(com.hospital.admin.entity.Users user, com.hospital.admin.entity.Modules module, com.hospital.admin.entity.Roles role);
}

