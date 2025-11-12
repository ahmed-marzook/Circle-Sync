package com.circlesync.circlesync.taskmodule.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Streak entity representing a user's streak for a task.
 */
@Entity
@Table(name = "streaks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Streak {

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

  @NotNull(message = "Current streak is required")
  @Column(name = "current_streak", nullable = false)
  private Integer currentStreak = 0;

  @NotNull(message = "Longest streak is required")
  @Column(name = "longest_streak", nullable = false)
  private Integer longestStreak = 0;

  @Column(name = "last_completed_date")
  private LocalDate lastCompletedDate;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
