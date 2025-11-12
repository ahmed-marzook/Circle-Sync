package com.circlesync.circlesync.taskmodule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new task.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {

  @NotNull(message = "Circle ID is required")
  private UUID circleId;

  @NotNull(message = "Creator ID is required")
  private UUID createdBy;

  private UUID[] assignedTo;

  @NotBlank(message = "Title is required")
  @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
  private String title;

  private String description;

  @NotBlank(message = "Type is required (HABIT or TODO)")
  private String type;

  private String category;

  private String frequency;

  @NotBlank(message = "Visibility is required (PUBLIC, PRIVATE, or CIRCLE)")
  private String visibility;

  private Integer points;

  private LocalDate dueDate;

  private String[] tags;
}
