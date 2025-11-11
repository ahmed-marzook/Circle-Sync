package com.circlesync.circlesync.circlemodule.mapper;

import com.circlesync.circlesync.circlemodule.dto.CircleResponse;
import com.circlesync.circlesync.circlemodule.dto.CreateCircleRequest;
import com.circlesync.circlesync.circlemodule.dto.UpdateCircleRequest;
import com.circlesync.circlesync.circlemodule.entity.Circle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CircleMapper {

    /**
     * Convert CreateCircleRequest to Circle entity
     * Ignores id, createdBy, createdAt, updatedAt, inviteCode (set by service)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "inviteCode", ignore = true)
    Circle toEntity(CreateCircleRequest request);

    /**
     * Convert Circle entity to CircleResponse
     * memberCount and currentUserRole are set manually in service
     */
    @Mapping(target = "memberCount", ignore = true)
    @Mapping(target = "currentUserRole", ignore = true)
    CircleResponse toResponse(Circle circle);

    /**
     * Helper method to convert Circle to CircleResponse with additional info
     */
    default CircleResponse toResponse(Circle circle, Integer memberCount, String currentUserRole) {
        CircleResponse response = toResponse(circle);
        response.setMemberCount(memberCount);
        response.setCurrentUserRole(currentUserRole);
        return response;
    }

    /**
     * Update entity from UpdateCircleRequest (full update)
     * Updates all non-null fields
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "inviteCode", ignore = true)
    void updateEntityFromRequest(UpdateCircleRequest request, @MappingTarget Circle circle);

    /**
     * Patch entity from UpdateCircleRequest (partial update)
     * Only updates non-null fields, ignores blank strings
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "inviteCode", ignore = true)
    @Mapping(target = "name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "circleType", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "privacy", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "avatarUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "settings", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntityFromRequest(UpdateCircleRequest request, @MappingTarget Circle circle);
}