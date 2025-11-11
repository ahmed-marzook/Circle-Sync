package com.circlesync.circlesync.circlemodule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCircleRequest {

    @NotBlank(message = "Circle name is required")
    @Size(min = 1, max = 255, message = "Circle name must be between 1 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotBlank(message = "Circle type is required")
    @Pattern(
            regexp = "FAMILY|FRIENDS|WORK|HOBBY|COMMUNITY|OTHER",
            message = "Circle type must be one of: FAMILY, FRIENDS, WORK, HOBBY, COMMUNITY, OTHER"
    )
    private String circleType;

    @NotBlank(message = "Privacy setting is required")
    @Pattern(
            regexp = "PUBLIC|PRIVATE|INVITE_ONLY",
            message = "Privacy must be one of: PUBLIC, PRIVATE, INVITE_ONLY"
    )
    private String privacy;

    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    private String avatarUrl;

    private Map<String, Object> settings;
}