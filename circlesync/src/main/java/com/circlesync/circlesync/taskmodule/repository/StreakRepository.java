package com.circlesync.circlesync.taskmodule.repository;

import com.circlesync.circlesync.taskmodule.entity.Streak;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Streak entity.
 */
@Repository
public interface StreakRepository extends JpaRepository<Streak, UUID> {

  /**
   * Find streak by task ID and user ID.
   *
   * @param taskId the task ID
   * @param userId the user ID
   * @return optional streak
   */
  Optional<Streak> findByTaskIdAndUserId(UUID taskId, UUID userId);

  /**
   * Find all streaks for a specific task.
   *
   * @param taskId the task ID
   * @return list of streaks
   */
  List<Streak> findByTaskId(UUID taskId);

  /**
   * Find all streaks for a specific user.
   *
   * @param userId the user ID
   * @return list of streaks
   */
  List<Streak> findByUserId(UUID userId);

  /**
   * Check if a streak exists for a task and user.
   *
   * @param taskId the task ID
   * @param userId the user ID
   * @return true if exists, false otherwise
   */
  boolean existsByTaskIdAndUserId(UUID taskId, UUID userId);

  /**
   * Delete all streaks for a task.
   *
   * @param taskId the task ID
   */
  void deleteByTaskId(UUID taskId);

  /**
   * Delete streak by task ID and user ID.
   *
   * @param taskId the task ID
   * @param userId the user ID
   */
  void deleteByTaskIdAndUserId(UUID taskId, UUID userId);
}
