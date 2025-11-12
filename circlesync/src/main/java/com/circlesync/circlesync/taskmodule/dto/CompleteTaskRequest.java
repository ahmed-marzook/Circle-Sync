package com.circlesync.circlesync.taskmodule.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for completing a task.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteTaskRequest {

  @NotNull(message = "User ID is required")
  private UUID userId;

  private String notes;

  private LocalDate date; // Optional, defaults to today if not provided
}
