package com.hospital.admin.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department  extends BaseEntity{
	  @Id
	    @GeneratedValue
	    private UUID id;
	    
	    private String name;
	    
	    private Boolean isActive;
	    
	    private UUID moduleId;
	    
	    
}
