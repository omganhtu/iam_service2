package com.example.iam_service2.mapper;

import com.example.iam_service2.dto.response.PermissionResponse;
import com.example.iam_service2.entity.Permission;

public class PermissionMapper {

    public static PermissionResponse toResponse(Permission permission) {
        PermissionResponse dto = new PermissionResponse();
        dto.setId(permission.getId());
        dto.setResourceCode(permission.getResourceCode());
        dto.setScope(permission.getScope());
        dto.setDescription(permission.getDescription());
        return dto;
    }
}
