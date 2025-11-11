package com.circlesync.circlesync.circlemodule.controller;

import com.circlesync.circlesync.circlemodule.dto.AddMemberRequest;
import com.circlesync.circlesync.circlemodule.dto.CircleResponse;
import com.circlesync.circlesync.circlemodule.dto.CreateCircleRequest;
import com.circlesync.circlesync.circlemodule.dto.JoinCircleRequest;
import com.circlesync.circlesync.circlemodule.dto.MemberResponse;
import com.circlesync.circlesync.circlemodule.dto.UpdateCircleRequest;
import com.circlesync.circlesync.circlemodule.dto.UpdateMemberRequest;
import com.circlesync.circlesync.circlemodule.service.CircleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/circles")
public class CircleController {

    private final CircleService circleService;

    public CircleController(CircleService circleService) {
        this.circleService = circleService;
    }

    // ==================== Circle Management ====================

    /**
     * POST /api/circles - Create new circle
     * @param request Circle creation details
     * @return Created circle with generated invite code
     */
    @PostMapping
    public ResponseEntity<CircleResponse> createCircle(
            @Valid @RequestBody CreateCircleRequest request) {
        CircleResponse response = circleService.createCircle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/circles/{id} - Get circle details
     * @param id Circle UUID
     * @return Circle details
     */
    @GetMapping("/{id}")
    public ResponseEntity<CircleResponse> getCircleDetails(@PathVariable String id) {
        CircleResponse response = circleService.getCircleDetails(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/circles/{id} - Update circle (full update)
     * @param id Circle UUID
     * @param request Updated circle details
     * @return Updated circle
     */
    @PutMapping("/{id}")
    public ResponseEntity<CircleResponse> updateCircle(
            @PathVariable String id,
            @Valid @RequestBody UpdateCircleRequest request) {
        CircleResponse response = circleService.updateCircle(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/circles/{id} - Partial update circle
     * @param id Circle UUID
     * @param request Partial circle updates
     * @return Updated circle
     */
    @PatchMapping("/{id}")
    public ResponseEntity<CircleResponse> patchCircle(
            @PathVariable String id,
            @Valid @RequestBody UpdateCircleRequest request) {
        CircleResponse response = circleService.patchCircle(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/circles/{id} - Delete circle
     * @param id Circle UUID
     * @return No content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCircle(@PathVariable String id) {
        circleService.deleteCircle(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/circles/user/{userId} - Get user's circles
     * @param userId User UUID
     * @param role Optional filter by role (ADMIN, MEMBER, VIEWER)
     * @return List of circles user belongs to
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CircleResponse>> getUserCircles(
            @PathVariable String userId,
            @RequestParam(required = false) String role) {
        List<CircleResponse> response = circleService.getUserCircles(userId, role);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/circles - Search or list circles
     * @param name Optional name search filter
     * @param circleType Optional type filter
     * @param privacy Optional privacy filter
     * @return List of circles matching criteria
     */
    @GetMapping
    public ResponseEntity<List<CircleResponse>> searchCircles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String circleType,
            @RequestParam(required = false) String privacy) {
        List<CircleResponse> response = circleService.searchCircles(name, circleType, privacy);
        return ResponseEntity.ok(response);
    }

    // ==================== Circle Membership ====================

    /**
     * POST /api/circles/join/{code} - Join circle via invite code
     * @param code Invite code
     * @param request User details for joining
     * @return Circle details
     */
    @PostMapping("/join/{code}")
    public ResponseEntity<CircleResponse> joinCircleByCode(
            @PathVariable String code,
            @Valid @RequestBody JoinCircleRequest request) {
        CircleResponse response = circleService.joinCircleByCode(code, request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/circles/{id}/members - Add member to circle
     * @param id Circle UUID
     * @param request Member details
     * @return Added member details
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<MemberResponse> addMember(
            @PathVariable String id,
            @Valid @RequestBody AddMemberRequest request) {
        MemberResponse response = circleService.addMember(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/circles/{id}/members - Get circle members
     * @param id Circle UUID
     * @param role Optional filter by role
     * @return List of circle members
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<MemberResponse>> getCircleMembers(
            @PathVariable String id,
            @RequestParam(required = false) String role) {
        List<MemberResponse> response = circleService.getCircleMembers(id, role);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/circles/{id}/members/{userId} - Get specific member details
     * @param id Circle UUID
     * @param userId User UUID
     * @return Member details
     */
    @GetMapping("/{id}/members/{userId}")
    public ResponseEntity<MemberResponse> getMemberDetails(
            @PathVariable String id,
            @PathVariable String userId) {
        MemberResponse response = circleService.getMemberDetails(id, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/circles/{id}/members/{userId} - Update member details
     * @param id Circle UUID
     * @param userId User UUID
     * @param request Updated member details (role, nickname, avatar)
     * @return Updated member details
     */
    @PatchMapping("/{id}/members/{userId}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable String id,
            @PathVariable String userId,
            @Valid @RequestBody UpdateMemberRequest request) {
        MemberResponse response = circleService.updateMember(id, userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/circles/{id}/members/{userId} - Remove member from circle
     * @param id Circle UUID
     * @param userId User UUID
     * @return No content
     */
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable String id,
            @PathVariable String userId) {
        circleService.removeMember(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/circles/{id}/leave - Leave circle (self-remove)
     * @param id Circle UUID
     * @param userId User UUID from authentication context
     * @return No content
     */
    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveCircle(
            @PathVariable String id,
            @RequestParam String userId) {
        circleService.leaveCircle(id, userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Invite Code Management ====================

    /**
     * POST /api/circles/{id}/regenerate-invite - Regenerate invite code
     * @param id Circle UUID
     * @return Circle with new invite code
     */
    @PostMapping("/{id}/regenerate-invite")
    public ResponseEntity<CircleResponse> regenerateInviteCode(@PathVariable String id) {
        CircleResponse response = circleService.regenerateInviteCode(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/circles/invite/{code} - Get circle info by invite code (preview)
     * @param code Invite code
     * @return Circle details (limited info for preview)
     */
    @GetMapping("/invite/{code}")
    public ResponseEntity<CircleResponse> getCircleByInviteCode(@PathVariable String code) {
        CircleResponse response = circleService.getCircleByInviteCode(code);
        return ResponseEntity.ok(response);
    }

    // ==================== Statistics ====================

    /**
     * GET /api/circles/{id}/stats - Get circle statistics
     * @param id Circle UUID
     * @return Circle statistics (member count, activity, etc.)
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<CircleStatsResponse> getCircleStats(@PathVariable String id) {
        CircleStatsResponse response = circleService.getCircleStats(id);
        return ResponseEntity.ok(response);
    }
}