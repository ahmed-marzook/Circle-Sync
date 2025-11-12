package com.circlesync.circlesync.taskmodule.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for streak data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreakResponse {

  private UUID id;
  private UUID taskId;
  private UUID userId;
  private Integer currentStreak;
  private Integer longestStreak;
  private LocalDate lastCompletedDate;
  private LocalDateTime updatedAt;
}
