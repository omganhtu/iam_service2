package com.example.iam_service2.service;

import com.example.iam_service2.entity.Permission;
import com.example.iam_service2.repository.PermissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<Permission> getAll() {
        return permissionRepository.findAll();
    }

    // ✅ NEW: Phân trang
    public Page<Permission> getPermissionsWithPagination(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }

    public Permission getById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
    }

    public Permission create(Permission permission) {
        boolean exists = permissionRepository.existsByResourceCodeAndScope(
                permission.getResourceCode(),
                permission.getScope()
        );
        if (exists) {
            throw new RuntimeException("Permission already exists for this resource and scope");
        }
        return permissionRepository.save(permission);
    }

    public Permission update(Long id, Permission updated) {
        Permission permission = getById(id);
        permission.setResourceCode(updated.getResourceCode());
        permission.setScope(updated.getScope());
        permission.setDescription(updated.getDescription());
        return permissionRepository.save(permission);
    }

    public void delete(Long id) {
        permissionRepository.deleteById(id);
    }
}
