package com.example.iam_service2.keycloak;

import com.example.iam_service2.exception.BadRequestException;
import com.example.iam_service2.exception.ConflictException;
import com.example.iam_service2.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class KeycloakService {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${keycloak.username}")
    private String adminUsername;

    @Value("${keycloak.password}")
    private String adminPassword;

    public String createUser(String username, String password, String email) {
        Keycloak keycloak = getKeycloakInstance();

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        user.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(realm).users().create(user);

        if (response.getStatus() == 201) {
            String userId = extractUserIdFromResponse(response);
            log.info("Tạo user trên Keycloak thành công: {}", userId);
            return userId;
        } else if (response.getStatus() == 409) {
            throw new ConflictException("User đã tồn tại trên Keycloak.");
        } else {
            throw new BadRequestException("Lỗi tạo user Keycloak: HTTP " + response.getStatus());
        }
    }

    public void logoutUser(String refreshToken) {
        String logoutUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(logoutUrl, request, Void.class);
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                log.info("Logout thành công khỏi Keycloak.");
            } else {
                log.warn("Logout Keycloak không thành công: HTTP {}", response.getStatusCode());
                throw new BadRequestException("Logout không thành công");
            }
        } catch (Exception ex) {
            log.error("Lỗi khi gọi logout Keycloak: {}", ex.getMessage());
            throw new BadRequestException("Lỗi khi logout Keycloak: " + ex.getMessage());
        }
    }

    /**
     * Đổi mật khẩu cho user theo username (dùng khi chưa có keycloakId)
     */
    public void resetPassword(String username, String newPassword) {
        Keycloak keycloak = getKeycloakInstance();
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        List<UserRepresentation> users = usersResource.search(username);
        if (users.isEmpty()) {
            throw new NotFoundException("Không tìm thấy user trong Keycloak");
        }

        String userId = users.get(0).getId();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);

        usersResource.get(userId).resetPassword(credential);
        log.info("Reset mật khẩu thành công cho user {}", username);
    }

    /**
     * Đổi mật khẩu bằng keycloakId (ưu tiên dùng)
     */
    public void changeUserPassword(String keycloakId, String newPassword) {
        Keycloak keycloak = getKeycloakInstance();
        UsersResource usersResource = keycloak.realm(realm).users();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);

        usersResource.get(keycloakId).resetPassword(credential);
        log.info("Đổi mật khẩu thành công cho user ID {}", keycloakId);
    }

    private Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master") // NOTE: Nếu bạn dùng realm khác, thay thế "master" bằng biến `realm`
                .clientId("admin-cli")
                .username(adminUsername)
                .password(adminPassword)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }

    private String extractUserIdFromResponse(Response response) {
        String location = response.getLocation().getPath();
        return location.substring(location.lastIndexOf("/") + 1);
    }
}
