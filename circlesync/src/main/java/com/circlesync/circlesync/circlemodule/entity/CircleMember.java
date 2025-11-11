package com.circlesync.circlesync.circlemodule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "circle_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"circle_id", "user_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Circle ID is required")
    @Column(name = "circle_id", nullable = false)
    private UUID circleId;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotBlank(message = "User name is required")
    @Size(min = 1, max = 255, message = "User name must be between 1 and 255 characters")
    @Column(name = "user_name", nullable = false)
    private String userName;

    @Size(max = 500, message = "User avatar URL must not exceed 500 characters")
    @Column(name = "user_avatar")
    private String userAvatar;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ADMIN|MEMBER|VIEWER",
            message = "Role must be one of: ADMIN, MEMBER, VIEWER")
    @Column(nullable = false)
    private String role;

    @Size(max = 100, message = "Nickname must not exceed 100 characters")
    @Column
    private String nickname;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Optional: Add ManyToOne relationship to Circle entity
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "circle_id", insertable = false, updatable = false)
    // private Circle circle;
}
