package com.circlesync.circlesync.taskmodule.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a task. All fields are optional for partial updates (PATCH).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {

  private UUID[] assignedTo;

  @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
  private String title;

  private String description;

  private String type;

  private String category;

  private String frequency;

  private String visibility;

  private Integer points;

  private String status;

  private LocalDate dueDate;

  private String[] tags;
}
