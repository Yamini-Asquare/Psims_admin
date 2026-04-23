package com.hospital.admin.request;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class RolePrivilegeRequest {
    private UUID roleId;
    private List<UUID> subPagePrivilageIds;
}

