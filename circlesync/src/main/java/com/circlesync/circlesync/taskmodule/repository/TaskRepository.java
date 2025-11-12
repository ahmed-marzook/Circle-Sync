package com.circlesync.circlesync.taskmodule.repository;

import com.circlesync.circlesync.taskmodule.entity.Task;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Task entity.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

  /**
   * Find all tasks by circle ID.
   *
   * @param circleId the circle ID
   * @return list of tasks
   */
  List<Task> findByCircleId(UUID circleId);

  /**
   * Find tasks by circle ID and status.
   *
   * @param circleId the circle ID
   * @param status the task status
   * @return list of tasks
   */
  List<Task> findByCircleIdAndStatus(UUID circleId, String status);

  /**
   * Find tasks by circle ID with due date today.
   *
   * @param circleId the circle ID
   * @param today today's date
   * @return list of tasks
   */
  @Query(
      "SELECT t FROM Task t WHERE t.circleId = :circleId AND t.dueDate = :today AND t.status ="
          + " 'ACTIVE'")
  List<Task> findByCircleIdAndDueDateToday(
      @Param("circleId") UUID circleId, @Param("today") LocalDate today);

  /**
   * Find tasks by creator ID.
   *
   * @param createdBy the creator user ID
   * @return list of tasks
   */
  List<Task> findByCreatedBy(UUID createdBy);

  /**
   * Find tasks by type.
   *
   * @param type the task type (HABIT or TODO)
   * @return list of tasks
   */
  List<Task> findByType(String type);

  /**
   * Find tasks assigned to a specific user.
   *
   * @param userId the user ID
   * @return list of tasks
   */
  @Query(value = "SELECT * FROM tasks WHERE :userId = ANY(assigned_to)", nativeQuery = true)
  List<Task> findByAssignedToContaining(@Param("userId") UUID userId);

  /**
   * Find tasks by circle ID and type.
   *
   * @param circleId the circle ID
   * @param type the task type
   * @return list of tasks
   */
  List<Task> findByCircleIdAndType(UUID circleId, String type);

  /**
   * Check if a task exists by ID.
   *
   * @param id the task ID
   * @return true if exists, false otherwise
   */
  boolean existsById(UUID id);

  /**
   * Delete all tasks by circle ID.
   *
   * @param circleId the circle ID
   */
  void deleteByCircleId(UUID circleId);
}
