package com.example.iam_service2.controller;

import com.example.iam_service2.entity.Role;
import com.example.iam_service2.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<Role>> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }

    // ✅ NEW: API phân trang
    @GetMapping("/paged")
    public ResponseEntity<List<Role>> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(roleService.getRolesWithPagination(PageRequest.of(page, size)).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Role> create(@RequestBody Role role) {
        Role created = roleService.create(role);
        return ResponseEntity.created(URI.create("/api/roles/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> update(@PathVariable Long id, @RequestBody Role role) {
        return ResponseEntity.ok(roleService.update(id, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/permissions")
    public ResponseEntity<Void> assignPermissions(
            @PathVariable Long id,
            @RequestBody Set<Long> permissionIds
    ) {
        roleService.assignPermissions(id, permissionIds);
        return ResponseEntity.ok().build();
    }
}
