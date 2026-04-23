package com.hospital.admin.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.admin.entity.UserToken;

public interface UserTokenRepository extends JpaRepository<UserToken, UUID> {
	  Optional<UserToken> findByToken(String token);
	    
	    List<UserToken> findAllByUserId(UUID userId);
	    
	    // Find all active tokens for a user
	    List<UserToken> findAllByUserIdAndIsRevokedFalse(UUID userId);
}
