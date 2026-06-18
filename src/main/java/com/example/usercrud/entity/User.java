package com.example.usercrud.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * 用户实体 — 录入系统的核心表
 *
 * Swagger Schema 示例: <a href="http://localhost:8080/swagger-ui.html">Swagger UI</a>
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
})
@Schema(description = "用户实体 — 录入系统的核心表")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "主键 ID, 自增", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 20)
    @Column(nullable = false, length = 20)
    @Schema(description = "用户名, 唯一, 2-20 字符", example = "alice", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank
    @Email
    @Column(nullable = false, length = 100)
    @Schema(description = "邮箱, 唯一, 合法 email 格式", example = "alice@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Size(max = 20)
    @Column(length = 20)
    @Schema(description = "手机号, 可选, ≤ 20 字符", example = "13800000001", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phone;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "创建时间, 自动维护", example = "2026-06-18T05:30:00Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @Schema(description = "更新时间, 自动维护", example = "2026-06-18T05:30:00Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // ===== getters / setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
