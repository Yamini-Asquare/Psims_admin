package com.hospital.admin.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.admin.entity.ModuleSubPage;
import com.hospital.admin.entity.Modules;
@Repository
public interface ModuleSubPageRepository extends JpaRepository<ModuleSubPage, UUID>{

	List<ModuleSubPage> findByModuleId(UUID moduleId);

	boolean existsBySubPageNameAndModule(String subPageName, Modules module);

	Optional<ModuleSubPage> findBySubPageNameAndModule(String subPageName, Modules module);

	boolean existsBySubPageName(String subPageName);

	Optional<ModuleSubPage> findBySubPageName(String subPageName);
	List<ModuleSubPage> findByModuleIdOrderBySubPageOrderAsc(UUID moduleId);	
}

