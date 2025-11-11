package com.circlesync.circlesync.circlemodule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {

    private UUID id;
    private UUID circleId;
    private UUID userId;
    private String userName;
    private String userAvatar;
    private String role;
    private String nickname;
    private LocalDateTime joinedAt;
    private LocalDateTime updatedAt;

    // Optional: Include additional user info
    private String email;
    private Boolean isOnline;
}