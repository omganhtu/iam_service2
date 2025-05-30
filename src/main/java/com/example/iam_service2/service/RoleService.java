package com.example.iam_service2.service;

import com.example.iam_service2.entity.Permission;
import com.example.iam_service2.entity.Role;
import com.example.iam_service2.repository.PermissionRepository;
import com.example.iam_service2.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    // ✅ NEW: Phân trang
    public Page<Role> getRolesWithPagination(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    public Role getById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    public Role create(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Role already exists");
        }
        return roleRepository.save(role);
    }

    public Role update(Long id, Role updatedRole) {
        Role role = getById(id);
        role.setName(updatedRole.getName());
        role.setDescription(updatedRole.getDescription());
        return roleRepository.save(role);
    }

    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    public void assignPermissions(Long roleId, Set<Long> permissionIds) {
        Role role = getById(roleId);
        Set<Permission> permissions = permissionRepository.findByIdIn(permissionIds);
        role.setPermissions(permissions);
        roleRepository.save(role);
    }
}
