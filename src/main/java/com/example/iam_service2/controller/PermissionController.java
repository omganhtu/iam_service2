package com.example.iam_service2.controller;

import com.example.iam_service2.dto.request.PermissionRequest;
import com.example.iam_service2.dto.response.PermissionResponse;
import com.example.iam_service2.entity.Permission;
import com.example.iam_service2.mapper.PermissionMapper;
import com.example.iam_service2.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAll();
        List<PermissionResponse> result = permissions.stream()
                .map(PermissionMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ✅ NEW: API phân trang
    @GetMapping("/paged")
    public ResponseEntity<List<PermissionResponse>> getPagedPermissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                permissionService.getPermissionsWithPagination(PageRequest.of(page, size))
                        .map(PermissionMapper::toResponse)
                        .getContent()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> getById(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        return ResponseEntity.ok(PermissionMapper.toResponse(permission));
    }

    @PostMapping
    public ResponseEntity<PermissionResponse> create(@RequestBody PermissionRequest request) {
        Permission permission = new Permission();
        permission.setResourceCode(request.getResourceCode());
        permission.setScope(request.getScope());
        permission.setDescription(request.getDescription());

        Permission created = permissionService.create(permission);
        return ResponseEntity.created(URI.create("/api/permissions/" + created.getId()))
                .body(PermissionMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponse> update(
            @PathVariable Long id,
            @RequestBody PermissionRequest request
    ) {
        Permission toUpdate = new Permission();
        toUpdate.setResourceCode(request.getResourceCode());
        toUpdate.setScope(request.getScope());
        toUpdate.setDescription(request.getDescription());

        Permission updated = permissionService.update(id, toUpdate);
        return ResponseEntity.ok(PermissionMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
