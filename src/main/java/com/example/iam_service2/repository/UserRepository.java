package com.example.iam_service2.repository;

import com.example.iam_service2.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm user theo username (chỉ nếu chưa bị xóa)
    Optional<User> findByUsernameAndDeletedFalse(String username);

    //  Kiểm tra username tồn tại trong DB IAM (bắt buộc dùng cho JWT Converter)
    boolean existsByUsername(String username);

    //  Tìm theo email
    Optional<User> findByEmail(String email);

    //  Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);

    //  Lấy danh sách user chưa bị xóa (phân trang)
    @Query("SELECT u FROM User u WHERE u.deleted = false")
    Page<User> findAllNotDeleted(Pageable pageable);

    //  Lấy danh sách user đang hoạt động và chưa bị xóa
    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.active = true")
    List<User> findAllActive();

    //  Tìm user theo username chứa keyword (không phân biệt hoa thường, không lấy user đã xóa)
    @Query("SELECT u FROM User u WHERE u.deleted = false AND LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByUsername(@Param("keyword") String keyword);

    Optional<User> findByUsername(String username);

}
