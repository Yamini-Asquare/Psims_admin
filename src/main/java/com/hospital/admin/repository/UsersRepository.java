package com.hospital.admin.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.admin.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {

	Optional<Users> findByEmpCode(String empCode);

	Optional<Users> findByEmail(String email);

}
