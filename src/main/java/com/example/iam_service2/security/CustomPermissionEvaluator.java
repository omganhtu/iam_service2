package com.example.iam_service2.security;

import com.example.iam_service2.entity.User;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object resourceCode, Object scope) {
        if (authentication == null || resourceCode == null || scope == null) return false;

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) return false;

        User user = (User) principal;

        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission ->
                        permission.getResourceCode().equals(resourceCode.toString()) &&
                                permission.getScope().equals(scope.toString())
                );
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false; // Không sử dụng overload này
    }
}
