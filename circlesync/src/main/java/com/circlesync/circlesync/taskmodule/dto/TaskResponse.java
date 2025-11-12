package com.circlesync.circlesync.taskmodule.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for task data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

  private UUID id;
  private UUID circleId;
  private UUID createdBy;
  private UUID[] assignedTo;
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
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
