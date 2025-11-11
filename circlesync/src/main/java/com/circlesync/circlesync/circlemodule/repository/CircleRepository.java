package com.circlesync.circlesync.circlemodule.repository;

import com.circlesync.circlesync.circlemodule.entity.Circle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CircleRepository extends JpaRepository<Circle, UUID> {

    /**
     * Find circle by invite code
     */
    Optional<Circle> findByInviteCode(String inviteCode);

    /**
     * Find circles created by a specific user
     */
    List<Circle> findByCreatedBy(UUID createdBy);

    /**
     * Find circles by type
     */
    List<Circle> findByCircleType(String circleType);

    /**
     * Find circles by privacy setting
     */
    List<Circle> findByPrivacy(String privacy);

    /**
     * Search circles with optional filters
     */
    @Query("SELECT c FROM Circle c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:circleType IS NULL OR c.circleType = :circleType) AND " +
            "(:privacy IS NULL OR c.privacy = :privacy)")
    List<Circle> searchCircles(
            @Param("name") String name,
            @Param("circleType") String circleType,
            @Param("privacy") String privacy
    );

    /**
     * Check if invite code exists
     */
    boolean existsByInviteCode(String inviteCode);

    /**
     * Find public circles
     */
    @Query("SELECT c FROM Circle c WHERE c.privacy = 'PUBLIC' ORDER BY c.createdAt DESC")
    List<Circle> findPublicCircles();
}