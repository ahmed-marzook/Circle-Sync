package com.circlesync.circlesync.taskmodule.service;

import com.circlesync.circlesync.taskmodule.dto.CompleteTaskRequest;
import com.circlesync.circlesync.taskmodule.dto.CreateTaskRequest;
import com.circlesync.circlesync.taskmodule.dto.StreakResponse;
import com.circlesync.circlesync.taskmodule.dto.TaskCompletionResponse;
import com.circlesync.circlesync.taskmodule.dto.TaskResponse;
import com.circlesync.circlesync.taskmodule.dto.UpdateTaskRequest;
import com.circlesync.circlesync.taskmodule.entity.Streak;
import com.circlesync.circlesync.taskmodule.entity.Task;
import com.circlesync.circlesync.taskmodule.entity.TaskCompletion;
import com.circlesync.circlesync.taskmodule.exception.TaskNotFoundException;
import com.circlesync.circlesync.taskmodule.mapper.StreakMapper;
import com.circlesync.circlesync.taskmodule.mapper.TaskCompletionMapper;
import com.circlesync.circlesync.taskmodule.mapper.TaskMapper;
import com.circlesync.circlesync.taskmodule.repository.StreakRepository;
import com.circlesync.circlesync.taskmodule.repository.TaskCompletionRepository;
import com.circlesync.circlesync.taskmodule.repository.TaskRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of TaskService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;
  private final TaskCompletionRepository taskCompletionRepository;
  private final StreakRepository streakRepository;
  private final TaskMapper taskMapper;
  private final TaskCompletionMapper taskCompletionMapper;
  private final StreakMapper streakMapper;

  @Override
  public TaskResponse createTask(CreateTaskRequest request) {
    log.info("Creating task: {}", request.getTitle());

    Task task = taskMapper.toEntity(request);
    task.setStatus("ACTIVE");

    if (task.getPoints() == null) {
      task.setPoints(0);
    }

    Task savedTask = taskRepository.save(task);
    log.info("Task created successfully with ID: {}", savedTask.getId());

    return taskMapper.toResponse(savedTask);
  }

  @Override
  @Transactional(readOnly = true)
  public TaskResponse getTaskById(String id) {
    log.info("Fetching task with ID: {}", id);

    UUID taskId = UUID.fromString(id);
    Task task =
        taskRepository
            .findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));

    return taskMapper.toResponse(task);
  }

  @Override
  public TaskResponse updateTask(String id, UpdateTaskRequest request) {
    log.info("Updating task with ID: {}", id);

    UUID taskId = UUID.fromString(id);
    Task task =
        taskRepository
            .findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));

    taskMapper.patchEntityFromRequest(request, task);
    Task updatedTask = taskRepository.save(task);

    log.info("Task updated successfully: {}", id);
    return taskMapper.toResponse(updatedTask);
  }

  @Override
  public void deleteTask(String id) {
    log.info("Deleting task with ID: {}", id);

    UUID taskId = UUID.fromString(id);
    if (!taskRepository.existsById(taskId)) {
      throw new TaskNotFoundException("Task not found with ID: " + id);
    }

    taskRepository.deleteById(taskId);
    log.info("Task deleted successfully: {}", id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TaskResponse> getCircleTasks(String circleId) {
    log.info("Fetching tasks for circle: {}", circleId);

    UUID uuid = UUID.fromString(circleId);
    List<Task> tasks = taskRepository.findByCircleIdAndStatus(uuid, "ACTIVE");

    return tasks.stream().map(taskMapper::toResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<TaskResponse> getCircleTodayTasks(String circleId) {
    log.info("Fetching today's tasks for circle: {}", circleId);

    UUID uuid = UUID.fromString(circleId);
    LocalDate today = LocalDate.now();
    List<Task> tasks = taskRepository.findByCircleIdAndDueDateToday(uuid, today);

    return tasks.stream().map(taskMapper::toResponse).collect(Collectors.toList());
  }

  @Override
  public TaskCompletionResponse completeTask(String id, CompleteTaskRequest request) {
    log.info("Completing task: {} by user: {}", id, request.getUserId());

    UUID taskId = UUID.fromString(id);
    Task task =
        taskRepository
            .findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));

    // Set default date if not provided
    LocalDate completionDate = request.getDate() != null ? request.getDate() : LocalDate.now();
    request.setDate(completionDate);

    // Check if already completed today
    boolean alreadyCompleted =
        taskCompletionRepository.existsByTaskIdAndUserIdAndDate(
            taskId, request.getUserId(), completionDate);

    if (alreadyCompleted) {
      log.warn("Task already completed on this date");
      // Return existing completion
      TaskCompletion existing =
          taskCompletionRepository
              .findByTaskIdAndUserIdAndDate(taskId, request.getUserId(), completionDate)
              .orElseThrow();
      return taskCompletionMapper.toResponse(existing);
    }

    // Create completion record
    TaskCompletion completion = taskCompletionMapper.toEntity(request, taskId);
    TaskCompletion savedCompletion = taskCompletionRepository.save(completion);

    // Update or create streak
    updateStreak(taskId, request.getUserId(), completionDate);

    log.info("Task completed successfully: {}", id);
    return taskCompletionMapper.toResponse(savedCompletion);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TaskCompletionResponse> getTaskCompletions(String id) {
    log.info("Fetching completions for task: {}", id);

    UUID taskId = UUID.fromString(id);
    if (!taskRepository.existsById(taskId)) {
      throw new TaskNotFoundException("Task not found with ID: " + id);
    }

    List<TaskCompletion> completions = taskCompletionRepository.findByTaskId(taskId);

    return completions.stream().map(taskCompletionMapper::toResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public StreakResponse getUserStreak(String taskId, String userId) {
    log.info("Fetching streak for task: {} and user: {}", taskId, userId);

    UUID taskUuid = UUID.fromString(taskId);
    UUID userUuid = UUID.fromString(userId);

    if (!taskRepository.existsById(taskUuid)) {
      throw new TaskNotFoundException("Task not found with ID: " + taskId);
    }

    Streak streak =
        streakRepository
            .findByTaskIdAndUserId(taskUuid, userUuid)
            .orElseGet(
                () -> {
                  // Create empty streak if doesn't exist
                  Streak newStreak = new Streak();
                  newStreak.setTaskId(taskUuid);
                  newStreak.setUserId(userUuid);
                  newStreak.setCurrentStreak(0);
                  newStreak.setLongestStreak(0);
                  newStreak.setLastCompletedDate(null);
                  return newStreak;
                });

    return streakMapper.toResponse(streak);
  }

  /**
   * Update or create streak for a user's task completion.
   *
   * @param taskId the task ID
   * @param userId the user ID
   * @param completionDate the completion date
   */
  private void updateStreak(UUID taskId, UUID userId, LocalDate completionDate) {
    Streak streak =
        streakRepository
            .findByTaskIdAndUserId(taskId, userId)
            .orElseGet(
                () -> {
                  Streak newStreak = new Streak();
                  newStreak.setTaskId(taskId);
                  newStreak.setUserId(userId);
                  newStreak.setCurrentStreak(0);
                  newStreak.setLongestStreak(0);
                  return newStreak;
                });

    LocalDate lastCompleted = streak.getLastCompletedDate();

    if (lastCompleted == null) {
      // First completion
      streak.setCurrentStreak(1);
      streak.setLongestStreak(1);
    } else {
      long daysBetween = ChronoUnit.DAYS.between(lastCompleted, completionDate);

      if (daysBetween == 1) {
        // Consecutive day - increment streak
        streak.setCurrentStreak(streak.getCurrentStreak() + 1);
        if (streak.getCurrentStreak() > streak.getLongestStreak()) {
          streak.setLongestStreak(streak.getCurrentStreak());
        }
      } else if (daysBetween > 1) {
        // Streak broken - reset to 1
        streak.setCurrentStreak(1);
      }
      // If daysBetween == 0, it means same day completion (already handled by duplicate check)
      // If daysBetween < 0, it means backdated completion - we still update the streak but don't
      // increment
    }

    streak.setLastCompletedDate(completionDate);
    streakRepository.save(streak);
    log.info(
        "Streak updated for task: {} and user: {}. Current: {}, Longest: {}",
        taskId,
        userId,
        streak.getCurrentStreak(),
        streak.getLongestStreak());
  }
}
