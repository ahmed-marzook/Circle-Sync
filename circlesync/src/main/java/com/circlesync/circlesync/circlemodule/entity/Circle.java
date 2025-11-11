package com.circlesync.circlesync.circlemodule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "circles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Circle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Circle name is required")
    @Size(min = 1, max = 255, message = "Circle name must be between 1 and 255 characters")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Circle type is required")
    @Pattern(regexp = "FAMILY|FRIENDS|WORK|HOBBY|COMMUNITY|OTHER",
            message = "Circle type must be one of: FAMILY, FRIENDS, WORK, HOBBY, COMMUNITY, OTHER")
    @Column(name = "circle_type", nullable = false)
    private String circleType;

    @Size(min = 6, max = 12, message = "Invite code must be between 6 and 12 characters")
    @Column(name = "invite_code", unique = true)
    private String inviteCode;

    @NotBlank(message = "Privacy setting is required")
    @Pattern(regexp = "PUBLIC|PRIVATE|INVITE_ONLY",
            message = "Privacy must be one of: PUBLIC, PRIVATE, INVITE_ONLY")
    @Column(nullable = false)
    private String privacy;

    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    @Column(name = "avatar_url")
    private String avatarUrl;

    @NotNull(message = "Creator ID is required")
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> settings;
}

