package com.example.iam_service2.dto.request;

import lombok.Data;

@Data
public class PermissionRequest {
    private String resourceCode;
    private String scope;
    private String description;
}
