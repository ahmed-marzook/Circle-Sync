package com.circlesync.circlesync.circlemodule.mapper;

import com.circlesync.circlesync.circlemodule.dto.AddMemberRequest;
import com.circlesync.circlesync.circlemodule.dto.MemberResponse;
import com.circlesync.circlesync.circlemodule.dto.UpdateMemberRequest;
import com.circlesync.circlesync.circlemodule.entity.CircleMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MemberMapper {

    /**
     * Convert AddMemberRequest to CircleMember entity
     * circleId is passed as parameter and set manually
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "circleId", source = "circleId")
    @Mapping(target = "userId", source = "request.userId")
    @Mapping(target = "userName", source = "request.userName")
    @Mapping(target = "userAvatar", source = "request.userAvatar")
    @Mapping(target = "role", source = "request.role")
    @Mapping(target = "nickname", source = "request.nickname")
    @Mapping(target = "joinedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CircleMember toEntity(AddMemberRequest request, UUID circleId);

    /**
     * Convert CircleMember entity to MemberResponse
     * Optional fields (email, isOnline) are ignored and can be set by service
     */
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "isOnline", ignore = true)
    MemberResponse toResponse(CircleMember member);

    /**
     * Update entity from UpdateMemberRequest
     * Only updates non-null fields
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "circleId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "nickname", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userAvatar", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateMemberRequest request, @MappingTarget CircleMember member);
}