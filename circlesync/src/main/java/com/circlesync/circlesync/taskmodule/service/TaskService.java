package com.circlesync.circlesync.taskmodule.service;

import com.circlesync.circlesync.taskmodule.dto.CompleteTaskRequest;
import com.circlesync.circlesync.taskmodule.dto.CreateTaskRequest;
import com.circlesync.circlesync.taskmodule.dto.StreakResponse;
import com.circlesync.circlesync.taskmodule.dto.TaskCompletionResponse;
import com.circlesync.circlesync.taskmodule.dto.TaskResponse;
import com.circlesync.circlesync.taskmodule.dto.UpdateTaskRequest;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for task operations.
 */
public interface TaskService {

  /**
   * Create a new task.
   *
   * @param request the create task request
   * @return the created task response
   */
  TaskResponse createTask(CreateTaskRequest request);

  /**
   * Get task details by ID.
   *
   * @param id the task ID
   * @return the task response
   */
  TaskResponse getTaskById(String id);

  /**
   * Update a task (full update).
   *
   * @param id the task ID
   * @param request the update task request
   * @return the updated task response
   */
  TaskResponse updateTask(String id, UpdateTaskRequest request);

  /**
   * Delete a task by ID.
   *
   * @param id the task ID
   */
  void deleteTask(String id);

  /**
   * Get all tasks for a circle.
   *
   * @param circleId the circle ID
   * @return list of task responses
   */
  List<TaskResponse> getCircleTasks(String circleId);

  /**
   * Get today's tasks for a circle.
   *
   * @param circleId the circle ID
   * @return list of task responses
   */
  List<TaskResponse> getCircleTodayTasks(String circleId);

  /**
   * Complete a task.
   *
   * @param id the task ID
   * @param request the complete task request
   * @return the task completion response
   */
  TaskCompletionResponse completeTask(String id, CompleteTaskRequest request);

  /**
   * Get completion history for a task.
   *
   * @param id the task ID
   * @return list of task completion responses
   */
  List<TaskCompletionResponse> getTaskCompletions(String id);

  /**
   * Get a user's streak for a task.
   *
   * @param taskId the task ID
   * @param userId the user ID
   * @return the streak response
   */
  StreakResponse getUserStreak(String taskId, String userId);
}
