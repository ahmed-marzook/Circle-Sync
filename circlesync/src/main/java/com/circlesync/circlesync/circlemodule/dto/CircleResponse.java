package com.circlesync.circlesync.circlemodule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleResponse {

    private UUID id;
    private String name;
    private String description;
    private String circleType;
    private String inviteCode;
    private String privacy;
    private String avatarUrl;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> settings;

    // Optional: Include member count or current user's role
    private Integer memberCount;
    private String currentUserRole;
}