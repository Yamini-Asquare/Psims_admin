package com.hospital.admin.entity;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@jakarta.persistence.Table(name = "modules")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Modules  extends BaseEntity{

	    @Id
	    @GeneratedValue
	    private UUID id;
	 
	   private String moduleName;
	   
	   private Boolean isActive;
	   
	   @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
	    @JsonManagedReference  // prevents infinite loop
	    private List<Roles> roles;
	   
	   @OneToMany(mappedBy = "module", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	   @JsonManagedReference
	    private List<ModuleSubPage> subPages; 
}
