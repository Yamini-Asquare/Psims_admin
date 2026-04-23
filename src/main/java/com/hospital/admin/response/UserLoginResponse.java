package com.hospital.admin.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginResponse {
    private UUID id;
    private String username;
    private String email;
    private UUID userId;
}
