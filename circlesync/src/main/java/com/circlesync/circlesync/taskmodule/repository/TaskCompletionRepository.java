package com.circlesync.circlesync.taskmodule.repository;

import com.circlesync.circlesync.taskmodule.entity.TaskCompletion;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for TaskCompletion entity.
 */
@Repository
public interface TaskCompletionRepository extends JpaRepository<TaskCompletion, UUID> {

  /**
   * Find all completions for a specific task.
   *
   * @param taskId the task ID
   * @return list of completions
   */
  List<TaskCompletion> findByTaskId(UUID taskId);

  /**
   * Find all completions by a specific user.
   *
   * @param userId the user ID
   * @return list of completions
   */
  List<TaskCompletion> findByUserId(UUID userId);

  /**
   * Find completions by task ID and user ID.
   *
   * @param taskId the task ID
   * @param userId the user ID
   * @return list of completions
   */
  List<TaskCompletion> findByTaskIdAndUserId(UUID taskId, UUID userId);

  /**
   * Find completion by task ID, user ID, and date.
   *
   * @param taskId the task ID
   * @param userId the user ID
   * @param date the date
   * @return optional completion
   */
  Optional<TaskCompletion> findByTaskIdAndUserIdAndDate(UUID taskId, UUID userId, LocalDate date);

  /**
   * Check if a task was completed by a user on a specific date.
   *
   * @param taskId the task ID
   * @param userId the user ID
   * @param date the date
   * @return true if completed, false otherwise
   */
  boolean existsByTaskIdAndUserIdAndDate(UUID taskId, UUID userId, LocalDate date);

  /**
   * Count completions for a task by a user.
   *
   * @param taskId the task ID
   * @param userId the user ID
   * @return count of completions
   */
  long countByTaskIdAndUserId(UUID taskId, UUID userId);

  /**
   * Delete all completions for a task.
   *
   * @param taskId the task ID
   */
  void deleteByTaskId(UUID taskId);

  /**
   * Find completions for a task within a date range.
   *
   * @param taskId the task ID
   * @param startDate start date
   * @param endDate end date
   * @return list of completions
   */
  @Query(
      "SELECT tc FROM TaskCompletion tc WHERE tc.taskId = :taskId AND tc.date BETWEEN :startDate"
          + " AND :endDate ORDER BY tc.date DESC")
  List<TaskCompletion> findByTaskIdAndDateBetween(
      @Param("taskId") UUID taskId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
}
