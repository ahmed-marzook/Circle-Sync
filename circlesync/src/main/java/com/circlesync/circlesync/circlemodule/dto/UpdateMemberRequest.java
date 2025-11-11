package com.circlesync.circlesync.circlemodule.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberRequest {

    @Pattern(
            regexp = "ADMIN|MEMBER|VIEWER",
            message = "Role must be one of: ADMIN, MEMBER, VIEWER"
    )
    private String role;

    @Size(max = 100, message = "Nickname must not exceed 100 characters")
    private String nickname;

    @Size(max = 500, message = "User avatar URL must not exceed 500 characters")
    private String userAvatar;
}