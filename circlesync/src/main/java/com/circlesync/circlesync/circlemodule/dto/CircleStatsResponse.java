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
public class CircleStatsResponse {

    private UUID circleId;
    private String circleName;

    // Member statistics
    private Integer totalMembers;
    private Integer activeMembers;
    private Integer adminCount;
    private Integer memberCount;
    private Integer viewerCount;

    // Activity statistics
    private LocalDateTime lastActivityAt;
    private LocalDateTime createdAt;
    private Integer daysActive;

    // Role breakdown
    private Map<String, Integer> membersByRole;

    // Optional: Additional metrics
    private Integer newMembersLastWeek;
    private Integer newMembersLastMonth;
    private Double averageMemberDuration; // Average days as member
}