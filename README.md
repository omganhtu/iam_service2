# 🛡️ IAM Service 2 – Identity & Access Management System

Hệ thống quản lý xác thực, phân quyền người dùng tích hợp Keycloak và Spring Boot.
Cho phép bật/tắt Keycloak linh hoạt, hỗ trợ RBAC, ghi log toàn cục, audit người thao tác và phân trang RESTful API.

##  Công nghệ sử dụng

| Công nghệ         | Mô tả |
|-------------------|------|
| **Spring Boot 3**       | Framework chính |
| **Spring Security**     | Xác thực và phân quyền |
| **Keycloak**            | Identity Provider |
| **JPA + Hibernate**     | ORM và truy vấn DB |
| **PostgreSQL**          | Hệ quản trị CSDL |
| **Spring Data JPA**     | Repository Layer |
| **SpringDoc OpenAPI**   | Sinh Swagger UI |
| **Lombok**              | Rút gọn code DTO/Entity |
| **Slf4j + Logback**     | Logging nâng cao |
| **AuditorAware**        | Ghi nhận người thay đổi |
| **@PreAuthorize + PermissionEvaluator** | Kiểm soát truy cập động |
| **PgAdmin**             | Quản lý CSDL PostgreSQL |

## ⚙ Các chức năng nổi bật

###  1. Tích hợp Keycloak làm Identity Server
- Đăng nhập qua access token của Keycloak
- Tự động tạo user và đồng bộ thông tin vào DB nội bộ
- Cho phép bật/tắt Keycloak qua cấu hình `auth.mode`

###  2. Quản lý người dùng
- Tạo, cập nhật, xoá mềm, khoá/mở user
- Gán vai trò cho user
- Phân trang danh sách user
- Đổi mật khẩu qua Keycloak ✅

###  3. Phân quyền RBAC
- CRUD quyền (`Permission`)
- CRUD vai trò (`Role`)
- Gán quyền cho vai trò
- Gán vai trò cho user
- Phân quyền động `@PreAuthorize("hasPermission(...)")`

### ️ 4. Xoá mềm
- Dùng `deleted = true`, không xoá vật lý dữ liệu

###  5. AuditorAware
- Ghi nhận `createdBy` và `updatedBy` trong các thao tác thay đổi dữ liệu

###  6. Pagination
- Áp dụng phân trang cho: User, Role, Permission

###  7. Swagger UI
- Tích hợp tài liệu API tự động tại `/swagger-ui.html`

###  8. Logging toàn cục
- Log request, response, exception
- Log rolling theo ngày
- Ẩn trường nhạy cảm như `password` trong log

##  Một số chức năng nâng cao (tuỳ chọn)
-  Đổi mật khẩu qua API cho người dùng Keycloak




## 📁 Cấu trúc thư mục

```
src/
├── main/java/com/example/iam_service2/
│   ├── config/            // Logging, Audit, cấu hình toàn cục
│   ├── controller/        // REST Controller
│   ├── dto/               // DTO request/response
│   ├── entity/            // JPA Entities
│   ├── exception/         // Custom exceptions
│   ├── keycloak/          // Tích hợp Keycloak Admin API
│   ├── mapper/            // DTO ↔ Entity
│   ├── repository/        // Spring Data JPA Repository
│   ├── security/          // PermissionEvaluator, config
│   └── service/           // Business logic
├── resources/
│   ├── application.properties
│   ├── logback-spring.xml
```

## ❤️ Những phần team tâm đắc

- ✅ Phân quyền động theo `resource + scope` như `hasPermission("user", "delete")`
- ✅ Giao tiếp với Keycloak hoàn toàn bằng Java client
- ✅ Ghi log toàn cục nhưng vẫn giữ bảo mật thông tin nhạy cảm
- ✅ Tự động đồng bộ user từ Keycloak về DB hệ thống