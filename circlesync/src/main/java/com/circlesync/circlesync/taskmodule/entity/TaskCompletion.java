package com.circlesync.circlesync.taskmodule.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

/**
 * TaskCompletion entity representing a completion record for a task.
 */
@Entity
@Table(name = "task_completions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletion {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotNull(message = "Task ID is required")
  @Column(name = "task_id", nullable = false)
  private UUID taskId;

  @NotNull(message = "User ID is required")
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @CreationTimestamp
  @Column(name = "completed_at", nullable = false)
  private LocalDateTime completedAt;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @NotNull(message = "Date is required")
  @Column(name = "date", nullable = false)
  private LocalDate date;
}
