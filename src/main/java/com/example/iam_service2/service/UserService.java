package com.example.iam_service2.service;

import com.example.iam_service2.dto.request.UserRequest;
import com.example.iam_service2.entity.Role;
import com.example.iam_service2.entity.User;
import com.example.iam_service2.keycloak.KeycloakService;
import com.example.iam_service2.mapper.UserMapper;
import com.example.iam_service2.repository.RoleRepository;
import com.example.iam_service2.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * Phân trang toàn bộ user chưa bị xóa mềm
     */
    public Page<User> getUsersWithPagination(Pageable pageable) {
        return userRepository.findAllNotDeleted(pageable);
    }

    /**
     * Lấy user theo ID nếu chưa bị xoá
     */
    public Optional<User> getById(Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted());
    }

    /**
     * Tạo user từ UserRequest
     */
    public User createFromRequest(UserRequest request) {
        User user = UserMapper.toEntity(request);

        // Gán roles nếu có
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = roleRepository.findByIdIn(request.getRoleIds());
            user.setRoles(roles);
        }

        user.setActive(true);
        user.setDeleted(false);
        return userRepository.save(user);
    }

    /**
     * Cập nhật user từ request
     */
    public User updateFromRequest(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Cập nhật role nếu có
        if (request.getRoleIds() != null) {
            Set<Role> roles = roleRepository.findByIdIn(request.getRoleIds());
            user.setRoles(roles);
        }

        return userRepository.save(user);
    }

    /**
     * Xoá mềm user
     */
    public void softDelete(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setDeleted(true);
            userRepository.save(user);
        });
    }

    /**
     * Khóa tài khoản
     */
    public void lockUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }

    /**
     * Mở khóa tài khoản
     */
    public void unlockUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(true);
            userRepository.save(user);
        });
    }

    /**
     * Gán vai trò cho user
     */
    public void assignRoles(Long userId, Set<Long> roleIds) {
        userRepository.findById(userId).ifPresent(user -> {
            Set<Role> roles = roleRepository.findByIdIn(roleIds);
            user.setRoles(roles);
            userRepository.save(user);
        });
    }

    /**
     * Kiểm tra email đã tồn tại
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
    /**
     * Đăng ký user bằng Keycloak và đồng bộ vào DB nội bộ
     */
    public User registerUserViaKeycloak(UserRequest request, KeycloakService keycloakService) {
        // Kiểm tra username/email đã tồn tại trong DB
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại.");
        }

        // Tạo user trên Keycloak
        String keycloakId = keycloakService.createUser(
                request.getUsername(),
                request.getPassword(),
                request.getEmail()
        );

        // Tạo user nội bộ
        User user = UserMapper.toEntity(request);
        user.setKeycloakId(keycloakId);
        user.setActive(true);
        user.setDeleted(false);

        // Gán roles
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = roleRepository.findByIdIn(request.getRoleIds());
            user.setRoles(roles);
        }

        return userRepository.save(user);
    }

}
