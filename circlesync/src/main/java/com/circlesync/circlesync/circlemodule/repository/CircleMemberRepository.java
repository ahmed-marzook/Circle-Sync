package com.circlesync.circlesync.circlemodule.repository;

import com.circlesync.circlesync.circlemodule.entity.CircleMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CircleMemberRepository extends JpaRepository<CircleMember, UUID> {

    /**
     * Find member by circle and user
     */
    Optional<CircleMember> findByCircleIdAndUserId(UUID circleId, UUID userId);

    /**
     * Find all members of a circle
     */
    List<CircleMember> findByCircleId(UUID circleId);

    /**
     * Find all circles a user belongs to
     */
    List<CircleMember> findByUserId(UUID userId);

    /**
     * Find members by circle and role
     */
    List<CircleMember> findByCircleIdAndRole(UUID circleId, String role);

    /**
     * Find members by user and role
     */
    List<CircleMember> findByUserIdAndRole(UUID userId, String role);

    /**
     * Count members in a circle
     */
    int countByCircleId(UUID circleId);

    /**
     * Count members with specific role in a circle
     */
    long countByCircleIdAndRole(UUID circleId, String role);

    /**
     * Delete all members of a circle
     */
    void deleteByCircleId(UUID circleId);

    /**
     * Check if user is member of circle
     */
    boolean existsByCircleIdAndUserId(UUID circleId, UUID userId);

    /**
     * Find admins of a circle
     */
    @Query("SELECT cm FROM CircleMember cm WHERE cm.circleId = :circleId AND cm.role = 'ADMIN'")
    List<CircleMember> findAdminsByCircleId(@Param("circleId") UUID circleId);

    /**
     * Get member count by role for a circle
     */
    @Query("SELECT cm.role, COUNT(cm) FROM CircleMember cm " +
            "WHERE cm.circleId = :circleId GROUP BY cm.role")
    List<Object[]> countMembersByRole(@Param("circleId") UUID circleId);
}