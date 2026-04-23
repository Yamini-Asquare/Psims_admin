package com.hospital.admin.request;

import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Employee code is required")
    private String empCode;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Contact is required")
    private Long contact;

    @NotEmpty(message = "At least one module-role assignment is required")
    private List<ModuleRoleRequest> moduleRoles;

    private String workLocation; // plain string, no validation needed

    private UUID department; // optional, but validated if provided

    private Boolean isActive;
}
