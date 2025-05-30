package com.example.iam_service2.controller;

import com.example.iam_service2.dto.request.ChangePasswordRequest;
import com.example.iam_service2.dto.request.PasswordResetRequest;
import com.example.iam_service2.dto.request.UserRequest;
import com.example.iam_service2.dto.response.UserResponse;
import com.example.iam_service2.entity.User;
import com.example.iam_service2.exception.NotFoundException;
import com.example.iam_service2.keycloak.KeycloakService;
import com.example.iam_service2.mapper.UserMapper;
import com.example.iam_service2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KeycloakService keycloakService;

    // ===== LẤY DANH SÁCH USER PHÂN TRANG =====
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<User> users = userService.getUsersWithPagination(PageRequest.of(page, size));
        List<UserResponse> result = users.getContent()
                .stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ===== LẤY USER THEO ID =====
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        User user = userService.getById(id)
                .orElseThrow(() -> new NotFoundException("User không tồn tại với ID: " + id));
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }

    // ===== TẠO USER TRONG DB (KHÔNG QUA KEYCLOAK) =====
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        User created = userService.createFromRequest(request);
        return ResponseEntity.created(URI.create("/api/users/" + created.getId()))
                .body(UserMapper.toResponse(created));
    }

    // ===== ĐĂNG KÝ USER (KEYCLOAK + DB) =====
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest request) {
        User user = userService.registerUserViaKeycloak(request, keycloakService);
        return ResponseEntity.created(URI.create("/api/users/" + user.getId()))
                .body(UserMapper.toResponse(user));
    }

    // ===== CẬP NHẬT USER =====
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest request
    ) {
        User updated = userService.updateFromRequest(id, request);
        return ResponseEntity.ok(UserMapper.toResponse(updated));
    }

    // ===== XOÁ MỀM USER =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        userService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    // ===== KHÓA USER =====
    @PutMapping("/{id}/lock")
    public ResponseEntity<Void> lockUser(@PathVariable Long id) {
        userService.lockUser(id);
        return ResponseEntity.noContent().build();
    }

    // ===== MỞ KHÓA USER =====
    @PutMapping("/{id}/unlock")
    public ResponseEntity<Void> unlockUser(@PathVariable Long id) {
        userService.unlockUser(id);
        return ResponseEntity.noContent().build();
    }

    // ===== GÁN ROLE CHO USER =====
    @PutMapping("/{id}/roles")
    public ResponseEntity<Void> assignRoles(
            @PathVariable Long id,
            @RequestBody Set<Long> roleIds
    ) {
        userService.assignRoles(id, roleIds);
        return ResponseEntity.ok().build();
    }

    // ===== RESET MẬT KHẨU TRÊN KEYCLOAK =====
    @PutMapping("/{username}/reset-password")
    @PreAuthorize("hasPermission('user', 'update')")
    public ResponseEntity<Void> resetPassword(
            @PathVariable String username,
            @RequestBody PasswordResetRequest request
    ) {
        keycloakService.resetPassword(username, request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    // ===== ĐỔI MẬT KHẨU TRÊN KEYCLOAK THEO ID =====
    @PutMapping("/{id}/change-password")
    @PreAuthorize("hasPermission('user', 'update')")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request
    ) {
        User user = userService.getById(id)
                .orElseThrow(() -> new NotFoundException("User không tồn tại với ID: " + id));

        keycloakService.changeUserPassword(user.getKeycloakId(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

}
