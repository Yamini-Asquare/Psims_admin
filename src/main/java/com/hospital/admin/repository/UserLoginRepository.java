package com.hospital.admin.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.admin.entity.Department;
import com.hospital.admin.entity.UserLogin;
import com.hospital.admin.entity.Users;
@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, UUID> {

    Optional<UserLogin> findByUsername(String username);

    Optional<UserLogin> findByEmail(String email); 

    Optional<UserLogin> findByUserId(UUID userId);
}
