package com.example.iam_service2.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String address;
    private Set<Long> roleIds;
}
