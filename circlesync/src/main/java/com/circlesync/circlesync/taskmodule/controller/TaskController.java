package com.circlesync.circlesync.taskmodule.controller;

import com.circlesync.circlesync.taskmodule.dto.CompleteTaskRequest;
import com.circlesync.circlesync.taskmodule.dto.CreateTaskRequest;
import com.circlesync.circlesync.taskmodule.dto.StreakResponse;
import com.circlesync.circlesync.taskmodule.dto.TaskCompletionResponse;
import com.circlesync.circlesync.taskmodule.dto.TaskResponse;
import com.circlesync.circlesync.taskmodule.dto.UpdateTaskRequest;
import com.circlesync.circlesync.taskmodule.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for task operations.
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tasks", description = "Task management API")
public class TaskController {

  private final TaskService taskService;

  /**
   * Create a new task.
   *
   * @param request the create task request
   * @return the created task response
   */
  @PostMapping
  @Operation(summary = "Create new task", description = "Create a new task in a circle")
  public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
    log.info("POST /api/tasks - Creating new task: {}", request.getTitle());
    TaskResponse response = taskService.createTask(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Get task details by ID.
   *
   * @param id the task ID
   * @return the task response
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get task details", description = "Get details of a specific task by ID")
  public ResponseEntity<TaskResponse> getTaskById(@PathVariable String id) {
    log.info("GET /api/tasks/{} - Fetching task details", id);
    TaskResponse response = taskService.getTaskById(id);
    return ResponseEntity.ok(response);
  }

  /**
   * Update a task.
   *
   * @param id the task ID
   * @param request the update task request
   * @return the updated task response
   */
  @PutMapping("/{id}")
  @Operation(summary = "Update task", description = "Update an existing task")
  public ResponseEntity<TaskResponse> updateTask(
      @PathVariable String id, @Valid @RequestBody UpdateTaskRequest request) {
    log.info("PUT /api/tasks/{} - Updating task", id);
    TaskResponse response = taskService.updateTask(id, request);
    return ResponseEntity.ok(response);
  }

  /**
   * Delete a task.
   *
   * @param id the task ID
   * @return no content response
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete task", description = "Delete a task by ID")
  public ResponseEntity<Void> deleteTask(@PathVariable String id) {
    log.info("DELETE /api/tasks/{} - Deleting task", id);
    taskService.deleteTask(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Get all tasks for a circle.
   *
   * @param id the circle ID
   * @return list of task responses
   */
  @GetMapping("/circle/{id}")
  @Operation(summary = "Get circle's tasks", description = "Get all tasks for a specific circle")
  public ResponseEntity<List<TaskResponse>> getCircleTasks(@PathVariable String id) {
    log.info("GET /api/tasks/circle/{} - Fetching circle tasks", id);
    List<TaskResponse> responses = taskService.getCircleTasks(id);
    return ResponseEntity.ok(responses);
  }

  /**
   * Get today's tasks for a circle.
   *
   * @param id the circle ID
   * @return list of task responses
   */
  @GetMapping("/circle/{id}/today")
  @Operation(
      summary = "Get today's tasks",
      description = "Get tasks with due date today for a specific circle")
  public ResponseEntity<List<TaskResponse>> getCircleTodayTasks(@PathVariable String id) {
    log.info("GET /api/tasks/circle/{}/today - Fetching today's tasks", id);
    List<TaskResponse> responses = taskService.getCircleTodayTasks(id);
    return ResponseEntity.ok(responses);
  }

  /**
   * Complete a task.
   *
   * @param id the task ID
   * @param request the complete task request
   * @return the task completion response
   */
  @PostMapping("/{id}/complete")
  @Operation(summary = "Complete task", description = "Mark a task as completed")
  public ResponseEntity<TaskCompletionResponse> completeTask(
      @PathVariable String id, @Valid @RequestBody CompleteTaskRequest request) {
    log.info("POST /api/tasks/{}/complete - Completing task", id);
    TaskCompletionResponse response = taskService.completeTask(id, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Get completion history for a task.
   *
   * @param id the task ID
   * @return list of task completion responses
   */
  @GetMapping("/{id}/completions")
  @Operation(
      summary = "Get completion history",
      description = "Get all completion records for a task")
  public ResponseEntity<List<TaskCompletionResponse>> getTaskCompletions(@PathVariable String id) {
    log.info("GET /api/tasks/{}/completions - Fetching completion history", id);
    List<TaskCompletionResponse> responses = taskService.getTaskCompletions(id);
    return ResponseEntity.ok(responses);
  }

  /**
   * Get user's streak for a task.
   *
   * @param id the task ID
   * @param userId the user ID
   * @return the streak response
   */
  @GetMapping("/{id}/streak/user/{userId}")
  @Operation(
      summary = "Get user's streak",
      description = "Get streak information for a user on a specific task")
  public ResponseEntity<StreakResponse> getUserStreak(
      @PathVariable String id, @PathVariable String userId) {
    log.info("GET /api/tasks/{}/streak/user/{} - Fetching user streak", id, userId);
    StreakResponse response = taskService.getUserStreak(id, userId);
    return ResponseEntity.ok(response);
  }
}
