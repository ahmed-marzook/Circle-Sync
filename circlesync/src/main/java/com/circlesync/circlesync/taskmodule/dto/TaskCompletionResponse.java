package com.circlesync.circlesync.taskmodule.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for task completion data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletionResponse {

  private UUID id;
  private UUID taskId;
  private UUID userId;
  private LocalDateTime completedAt;
  private String notes;
  private LocalDate date;
}
