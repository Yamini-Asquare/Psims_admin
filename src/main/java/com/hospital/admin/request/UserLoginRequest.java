package com.hospital.admin.request;


import java.util.UUID;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserLoginRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
