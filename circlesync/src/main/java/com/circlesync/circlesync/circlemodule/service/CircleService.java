package com.circlesync.circlesync.circlemodule.service;

import com.circlesync.circlesync.circlemodule.dto.AddMemberRequest;
import com.circlesync.circlesync.circlemodule.dto.CircleResponse;
import com.circlesync.circlesync.circlemodule.dto.CircleStatsResponse;
import com.circlesync.circlesync.circlemodule.dto.CreateCircleRequest;
import com.circlesync.circlesync.circlemodule.dto.JoinCircleRequest;
import com.circlesync.circlesync.circlemodule.dto.MemberResponse;
import com.circlesync.circlesync.circlemodule.dto.UpdateCircleRequest;
import com.circlesync.circlesync.circlemodule.dto.UpdateMemberRequest;

import java.util.List;

public interface CircleService {

    // Circle Management
    CircleResponse createCircle(CreateCircleRequest request);

    CircleResponse getCircleDetails(String id);

    CircleResponse updateCircle(String id, UpdateCircleRequest request);

    CircleResponse patchCircle(String id, UpdateCircleRequest request);

    void deleteCircle(String id);

    List<CircleResponse> getUserCircles(String userId, String role);

    List<CircleResponse> searchCircles(String name, String circleType, String privacy);

    // Circle Membership
    CircleResponse joinCircleByCode(String code, JoinCircleRequest request);

    MemberResponse addMember(String circleId, AddMemberRequest request);

    List<MemberResponse> getCircleMembers(String circleId, String role);

    MemberResponse getMemberDetails(String circleId, String userId);

    MemberResponse updateMember(String circleId, String userId, UpdateMemberRequest request);

    void removeMember(String circleId, String userId);

    void leaveCircle(String circleId, String userId);

    // Invite Code Management
    CircleResponse regenerateInviteCode(String circleId);

    CircleResponse getCircleByInviteCode(String code);

    // Statistics
    CircleStatsResponse getCircleStats(String circleId);
}