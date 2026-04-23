package com.hospital.admin.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UserLogin")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserLogin extends BaseEntity{
    @Id
    @GeneratedValue
    private UUID id;

    private String username;

    private String email;

    private String password;
    
    private UUID userId;
    
//    private Boolean isActive  = true;

}
