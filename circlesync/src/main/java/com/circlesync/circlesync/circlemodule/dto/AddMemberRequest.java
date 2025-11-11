package com.circlesync.circlesync.circlemodule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddMemberRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "User name is required")
    @Size(min = 1, max = 255, message = "User name must be between 1 and 255 characters")
    private String userName;

    @Size(max = 500, message = "User avatar URL must not exceed 500 characters")
    private String userAvatar;

    @NotBlank(message = "Role is required")
    @Pattern(
            regexp = "ADMIN|MEMBER|VIEWER",
            message = "Role must be one of: ADMIN, MEMBER, VIEWER"
    )
    private String role;

    @Size(max = 100, message = "Nickname must not exceed 100 characters")
    private String nickname;
}