package com.example.iam_service2.dto.response;

import lombok.Data;

@Data
public class PermissionResponse {
    private Long id;
    private String resourceCode;
    private String scope;
    private String description;
}
