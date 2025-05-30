package com.example.iam_service2.controller;

import com.example.iam_service2.keycloak.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakService keycloakService;

    /**
     * Logout khỏi Keycloak bằng refresh_token
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String refreshToken) {
        keycloakService.logoutUser(refreshToken);
        return ResponseEntity.ok("Đăng xuất khỏi Keycloak thành công.");
    }
}
