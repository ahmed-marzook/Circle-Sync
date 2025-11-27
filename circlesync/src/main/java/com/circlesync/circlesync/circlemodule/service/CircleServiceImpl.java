package com.circlesync.circlesync.circlemodule.service;

import com.circlesync.circlesync.circlemodule.dto.AddMemberRequest;
import com.circlesync.circlesync.circlemodule.dto.CircleResponse;
import com.circlesync.circlesync.circlemodule.dto.CircleStatsResponse;
import com.circlesync.circlesync.circlemodule.dto.CreateCircleRequest;
import com.circlesync.circlesync.circlemodule.dto.JoinCircleRequest;
import com.circlesync.circlesync.circlemodule.dto.MemberResponse;
import com.circlesync.circlesync.circlemodule.dto.UpdateCircleRequest;
import com.circlesync.circlesync.circlemodule.dto.UpdateMemberRequest;
import com.circlesync.circlesync.circlemodule.entity.Circle;
import com.circlesync.circlesync.circlemodule.entity.CircleMember;
import com.circlesync.circlesync.circlemodule.exception.CircleNotFoundException;
import com.circlesync.circlesync.circlemodule.exception.DuplicateMemberException;
import com.circlesync.circlesync.circlemodule.exception.InvalidInviteCodeException;
import com.circlesync.circlesync.circlemodule.exception.MemberNotFoundException;
import com.circlesync.circlesync.circlemodule.exception.UnauthorizedException;
import com.circlesync.circlesync.circlemodule.mapper.CircleMapper;
import com.circlesync.circlesync.circlemodule.mapper.MemberMapper;
import com.circlesync.circlesync.circlemodule.repository.CircleMemberRepository;
import com.circlesync.circlesync.circlemodule.repository.CircleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CircleServiceImpl implements CircleService {

    private final CircleRepository circleRepository;
    private final CircleMemberRepository circleMemberRepository;
    private final CircleMapper circleMapper;
    private final MemberMapper memberMapper;
    private final InviteCodeGenerator inviteCodeGenerator;

    // ==================== Circle Management ====================

    @Override
    public CircleResponse createCircle(CreateCircleRequest request) {
        log.info("Creating new circle: {}", request.getName());

        // Get current user ID from security context
        UUID currentUserId = getCurrentUserId();

        // Create circle entity
        Circle circle = circleMapper.toEntity(request);
        circle.setCreatedBy(currentUserId);
        circle.setInviteCode(inviteCodeGenerator.generate());

        // Save circle
        Circle savedCircle = circleRepository.save(circle);

        // Add creator as admin member
        CircleMember creatorMember = CircleMember.builder()
                .circleId(savedCircle.getId())
                .userId(currentUserId)
                .userName(getCurrentUserName())
                .userAvatar(getCurrentUserAvatar())
                .role("ADMIN")
                .build();
        circleMemberRepository.save(creatorMember);

        log.info("Circle created successfully with ID: {}", savedCircle.getId());
        return circleMapper.toResponse(savedCircle, 1, "ADMIN");
    }

    @Override
    @Transactional(readOnly = true)
    public CircleResponse getCircleDetails(String id) {
        log.info("Fetching circle details for ID: {}", id);

        UUID circleId = UUID.fromString(id);
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new CircleNotFoundException("Circle not found with ID: " + id));

        // Get member count and current user's role
        int memberCount = circleMemberRepository.countByCircleId(circleId);
        String currentUserRole = getCurrentUserRole(circleId);

        return circleMapper.toResponse(circle, memberCount, currentUserRole);
    }

    @Override
    public CircleResponse updateCircle(String id, UpdateCircleRequest request) {
        log.info("Updating circle with ID: {}", id);

        UUID circleId = UUID.fromString(id);
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new CircleNotFoundException("Circle not found with ID: " + id));

        // Verify user is admin
        verifyUserIsAdmin(circleId);

        // Update all fields
        circleMapper.updateEntityFromRequest(request, circle);
        Circle updatedCircle = circleRepository.save(circle);

        int memberCount = circleMemberRepository.countByCircleId(circleId);
        String currentUserRole = getCurrentUserRole(circleId);

        log.info("Circle updated successfully: {}", id);
        return circleMapper.toResponse(updatedCircle, memberCount, currentUserRole);
    }

    @Override
    public CircleResponse patchCircle(String id, UpdateCircleRequest request) {
        log.info("Patching circle with ID: {}", id);

        UUID circleId = UUID.fromString(id);
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new CircleNotFoundException("Circle not found with ID: " + id));

        // Verify user is admin
        verifyUserIsAdmin(circleId);

        // Update only provided fields
        circleMapper.patchEntityFromRequest(request, circle);
        Circle updatedCircle = circleRepository.save(circle);

        int memberCount = circleMemberRepository.countByCircleId(circleId);
        String currentUserRole = getCurrentUserRole(circleId);

        log.info("Circle patched successfully: {}", id);
        return circleMapper.toResponse(updatedCircle, memberCount, currentUserRole);
    }

    @Override
    public void deleteCircle(String id) {
        log.info("Deleting circle with ID: {}", id);

        UUID circleId = UUID.fromString(id);
        Circle circle = circleRepository.findById(circleId)
                .orElseThrow(() -> new CircleNotFoundException("Circle not found with ID: " + id));

        // Verify user is admin
        verifyUserIsAdmin(circleId);

        // Delete all members first
        circleMemberRepository.deleteByCircleId(circleId);

        // Delete circle
        circleRepository.delete(circle);

        log.info("Circle deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CircleResponse> getUserCircles(String userId, String role) {
        log.info("Fetching circles for user: {} with role filter: {}", userId, role);

        UUID userUuid = UUID.fromString(userId);
        List<CircleMember> memberships;

        if (role != null && !role.isBlank()) {
            memberships = circleMemberRepository.findByUserIdAndRole(userUuid, role);
        } else {
            memberships = circleMemberRepository.findByUserId(userUuid);
        }

        return memberships.stream()
                .map(membership -> {
                    Circle circle = circleRepository.findById(membership.getCircleId())
                            .orElse(null);
                    if (circle == null) return null;

                    int memberCount = circleMemberRepository.countByCircleId(circle.getId());
                    return circleMapper.toResponse(circle, memberCount, membership.getRole());
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CircleResponse> searchCircles(String name, String circleType, String privacy) {
        log.info("Searching circles with filters - name: {}, type: {}, privacy: {}",
                name, circleType, privacy);

        List<Circle> circles = circleRepository.findAll();

        UUID currentUserId = getCurrentUserId();
        return circles.stream()
                .map(circle -> {
                    int memberCount = circleMemberRepository.countByCircleId(circle.getId());
                    String userRole = circleMemberRepository
                            .findByCircleIdAndUserId(circle.getId(), currentUserId)
                            .map(CircleMember::getRole)
                            .orElse(null);
                    return circleMapper.toResponse(circle, memberCount, userRole);
                })
                .collect(Collectors.toList());
    }

    // ==================== Circle Membership ====================

    @Override
    public CircleResponse joinCircleByCode(String code, JoinCircleRequest request) {
        log.info("User {} attempting to join circle with code: {}", request.getUserId(), code);

        Circle circle = circleRepository.findByInviteCode(code)
                .orElseThrow(() -> new InvalidInviteCodeException("Invalid invite code: " + code));

        // Check if user is already a member
        UUID userId = request.getUserId();
        if (circleMemberRepository.findByCircleIdAndUserId(circle.getId(), userId).isPresent()) {
            throw new DuplicateMemberException("User is already a member of this circle");
        }

        // Add user as member
        CircleMember member = CircleMember.builder()
                .circleId(circle.getId())
                .userId(userId)
                .userName(request.getUserName())
                .userAvatar(request.getUserAvatar())
                .role("MEMBER")
                .nickname(request.getNickname())
                .build();

        circleMemberRepository.save(member);

        int memberCount = circleMemberRepository.countByCircleId(circle.getId());
        log.info("User {} joined circle {} successfully", userId, circle.getId());

        return circleMapper.toResponse(circle, memberCount, "MEMBER");
    }

    @Override
    public MemberResponse addMember(String circleId, AddMemberRequest request) {
        log.info("Adding member {} to circle {}", request.getUserId(), circleId);

        UUID circleUuid = UUID.fromString(circleId);

        // Verify circle exists
        circleRepository.findById(circleUuid)
                .orElseThrow(() -> new CircleNotFoundException("Circle not found with ID: " + circleId));

        // Verify user is admin
        verifyUserIsAdmin(circleUuid);

        // Check if user is already a member
        if (circleMemberRepository.findByCircleIdAndUserId(circleUuid, request.getUserId()).isPresent()) {
            throw new DuplicateMemberException("User is already a member of this circle");
        }

        // Create member
        CircleMember member = memberMapper.toEntity(request, circleUuid);
        CircleMember savedMember = circleMemberRepository.save(member);

        log.info("Member {} added successfully to circle {}", request.getUserId(), circleId);
        return memberMapper.toResponse(savedMember);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getCircleMembers(String circleId, String role) {
        log.info("Fetching members for circle: {} with role filter: {}", circleId, role);

        UUID circleUuid = UUID.fromString(circleId);

        // Verify circle exists
        circleRepository.findById(circleUuid)
                .orElseThrow(() -> new CircleNotFoundException("Circle not found with ID: " + circleId));

        List<CircleMember> members;
        if (role != null && !role.isBlank()) {
            members = circleMemberRepository.findByCircleIdAndRole(circleUuid, role);
        } else {
            members = circleMemberRepository.findByCircleId(circleUuid);
        }

        return members.stream()
                .map(memberMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberDetails(String circleId, String userId) {
        log.info("Fetching member details for user {} in circle {}", userId, circleId);

        UUID circleUuid = UUID.fromString(circleId);
        UUID userUuid = UUID.fromString(userId);

        CircleMember member = circleMemberRepository.findByCircleIdAndUserId(circleUuid, userUuid)
                .orElseThrow(() -> new MemberNotFoundException(
                        "Member not found in circle: " + circleId));

        return memberMapper.toResponse(member);
    }

    @Override
    public MemberResponse updateMember(String circleId, String userId, UpdateMemberRequest request) {
        log.info("Updating member {} in circle {}", userId, circleId);

        UUID circleUuid = UUID.fromString(circleId);
        UUID userUuid = UUID.fromString(userId);

        // Verify user is admin (unless updating self)
        UUID currentUserId = getCurrentUserId();
        if (!currentUserId.equals(userUuid)) {
            verifyUserIsAdmin(circleUuid);
        }

        CircleMember member = circleMemberRepository.findByCircleIdAndUserId(circleUuid, userUuid)
                .orElseThrow(() -> new MemberNotFoundException(
                        "Member not found in circle: " + circleId));

        // Update fields
        memberMapper.updateEntityFromRequest(request, member);
        CircleMember updatedMember = circleMemberRepository.save(member);

        log.info("Member {} updated successfully in circle {}", userId, circleId);
        return memberMapper.toResponse(updatedMember);
    }

    @Override
    public void removeMember(String circleId, String userId) {
        log.info("Removing member {} from circle {}", userId, circleId);

        UUID circleUuid = UUID.fromString(circleId);
        UUID userUuid = UUID.fromString(userId);

        // Verify user is admin
        verifyUserIsAdmin(circleUuid);

        CircleMember member = circleMemberRepository.findByCircleIdAndUserId(circleUuid, userUuid)
                .orElseThrow(() -> new MemberNotFoundException(
                        "Member not found in circle: " + circleId));

        circleMemberRepository.delete(member);
        log.info("Member {} removed successfully from circle {}", userId, circleId);
    }

    @Override
    public void leaveCircle(String circleId, String userId) {
        log.info("User {} leaving circle {}", userId, circleId);

        UUID circleUuid = UUID.fromString(circleId);
        UUID userUuid = UUID.fromString(userId);

        CircleMember member = circleMemberRepository.findByCircleIdAndUserId(circleUuid, userUuid)
                .orElseThrow(() -> new MemberNotFoundException(
                        "Member not found in circle: " + circleId));

        // Check if user is the last admin
        if ("ADMIN".equals(member.getRole())) {
            long adminCount = circleMemberRepository.countByCircleIdAndRole(circleUuid, "ADMIN");
            if (adminCount <= 1) {
                throw new UnauthorizedException(
                        "Cannot leave circle as the last admin. Transfer admin rights or delete the circle.");
            }
        }

        circleMemberRepository.delete(member);
        log.info("User {} left circle {} successfully", userId, circleId);
    }

    // ==================== Invite Code Management ====================

    @Override
    public CircleResponse regenerateInviteCode(String circleId) {
        log.info("Regenerating invite code for circle: {}", circleId);

        UUID circleUuid = UUID.fromString(circleId);
        Circle circle = circleRepository.findById(circleUuid)
                .orElseThrow(() -> new CircleNotFoundException("Circle not found with ID: " + circleId));

        // Verify user is admin
        verifyUserIsAdmin(circleUuid);

        // Generate new invite code
        String newInviteCode = inviteCodeGenerator.generate();
        circle.setInviteCode(newInviteCode);
        Circle updatedCircle = circleRepository.save(circle);

        int memberCount = circleMemberRepository.countByCircleId(circleUuid);
        String currentUserRole = getCurrentUserRole(circleUuid);

        log.info("Invite code regenerated successfully for circle: {}", circleId);
        return circleMapper.toResponse(updatedCircle, memberCount, currentUserRole);
    }

    @Override
    @Transactional(readOnly = true)
    public CircleResponse getCircleByInviteCode(String code) {
        log.info("Fetching circle by invite code (preview)");

        Circle circle = circleRepository.findByInviteCode(code)
                .orElseThrow(() -> new InvalidInviteCodeException("Invalid invite code: " + code));

        int memberCount = circleMemberRepository.countByCircleId(circle.getId());

        // Return limited information for preview
        CircleResponse response = circleMapper.toResponse(circle, memberCount, null);
        response.setSettings(null); // Hide settings in preview
        response.setInviteCode(null); // Hide invite code in preview

        return response;
    }

    // ==================== Statistics ====================

    @Override
    @Transactional(readOnly = true)
    public CircleStatsResponse getCircleStats(String circleId) {
        log.info("Fetching statistics for circle: {}", circleId);

        UUID circleUuid = UUID.fromString(circleId);
        Circle circle = circleRepository.findById(circleUuid)
                .orElseThrow(() -> new CircleNotFoundException("Circle not found with ID: " + circleId));

        List<CircleMember> members = circleMemberRepository.findByCircleId(circleUuid);

        // Calculate statistics
        int totalMembers = members.size();
        long adminCount = members.stream().filter(m -> "ADMIN".equals(m.getRole())).count();
        long memberCount = members.stream().filter(m -> "MEMBER".equals(m.getRole())).count();
        long viewerCount = members.stream().filter(m -> "VIEWER".equals(m.getRole())).count();

        // Members by role map
        Map<String, Integer> membersByRole = new HashMap<>();
        membersByRole.put("ADMIN", (int) adminCount);
        membersByRole.put("MEMBER", (int) memberCount);
        membersByRole.put("VIEWER", (int) viewerCount);

        // Time-based statistics
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);
        LocalDateTime oneMonthAgo = now.minusMonths(1);

        int newMembersLastWeek = (int) members.stream()
                .filter(m -> m.getJoinedAt().isAfter(oneWeekAgo))
                .count();

        int newMembersLastMonth = (int) members.stream()
                .filter(m -> m.getJoinedAt().isAfter(oneMonthAgo))
                .count();

        // Calculate average member duration
        double avgDuration = members.stream()
                .mapToLong(m -> ChronoUnit.DAYS.between(m.getJoinedAt(), now))
                .average()
                .orElse(0.0);

        // Find last activity (most recent member join or update)
        LocalDateTime lastActivity = members.stream()
                .map(m -> m.getUpdatedAt() != null ? m.getUpdatedAt() : m.getJoinedAt())
                .max(LocalDateTime::compareTo)
                .orElse(circle.getCreatedAt());

        long daysActive = ChronoUnit.DAYS.between(circle.getCreatedAt(), now);

        return CircleStatsResponse.builder()
                .circleId(circle.getId())
                .circleName(circle.getName())
                .totalMembers(totalMembers)
                .activeMembers(totalMembers) // Could be enhanced with actual activity tracking
                .adminCount((int) adminCount)
                .memberCount((int) memberCount)
                .viewerCount((int) viewerCount)
                .membersByRole(membersByRole)
                .lastActivityAt(lastActivity)
                .createdAt(circle.getCreatedAt())
                .daysActive((int) daysActive)
                .newMembersLastWeek(newMembersLastWeek)
                .newMembersLastMonth(newMembersLastMonth)
                .averageMemberDuration(avgDuration)
                .build();
    }

    // ==================== Helper Methods ====================

    private UUID getCurrentUserId() {
        // TODO: Get from Spring Security context
        // return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        return UUID.randomUUID(); // Placeholder
    }

    private String getCurrentUserName() {
        // TODO: Get from Spring Security context
        return "Current User"; // Placeholder
    }

    private String getCurrentUserAvatar() {
        // TODO: Get from Spring Security context
        return null; // Placeholder
    }

    private String getCurrentUserRole(UUID circleId) {
        UUID currentUserId = getCurrentUserId();
        return circleMemberRepository.findByCircleIdAndUserId(circleId, currentUserId)
                .map(CircleMember::getRole)
                .orElse(null);
    }

    private void verifyUserIsAdmin(UUID circleId) {
        UUID currentUserId = getCurrentUserId();
        CircleMember member = circleMemberRepository.findByCircleIdAndUserId(circleId, currentUserId)
                .orElseThrow(() -> new UnauthorizedException("User is not a member of this circle"));

        if (!"ADMIN".equals(member.getRole())) {
            throw new UnauthorizedException("User must be an admin to perform this action");
        }
    }
}