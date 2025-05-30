package com.example.iam_service2.repository;

import com.example.iam_service2.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    // Tìm permission theo resource + scope
    Optional<Permission> findByResourceCodeAndScope(String resourceCode, String scope);

    // Kiểm tra trùng resource + scope
    boolean existsByResourceCodeAndScope(String resourceCode, String scope);

    // Tìm theo danh sách ID (dùng khi gán quyền cho role)
    Set<Permission> findByIdIn(Set<Long> ids);
}
