package com.circlesync.circlesync.taskmodule.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Task entity representing a task (habit or todo) in the system.
 */
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotNull(message = "Circle ID is required")
  @Column(name = "circle_id", nullable = false)
  private UUID circleId;

  @NotNull(message = "Creator ID is required")
  @Column(name = "created_by", nullable = false)
  private UUID createdBy;

  @Column(name = "assigned_to", columnDefinition = "uuid[]")
  private UUID[] assignedTo;

  @NotBlank(message = "Title is required")
  @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
  @Column(name = "title", nullable = false, length = 255)
  private String title;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @NotBlank(message = "Type is required")
  @Column(name = "type", nullable = false, length = 20)
  private String type; // HABIT or TODO

  @Column(name = "category", length = 50)
  private String category;

  @Column(name = "frequency", length = 50)
  private String frequency;

  @NotBlank(message = "Visibility is required")
  @Column(name = "visibility", nullable = false, length = 20)
  private String visibility; // PUBLIC, PRIVATE, CIRCLE

  @Column(name = "points")
  private Integer points = 0;

  @Column(name = "status", nullable = false, length = 20)
  private String status = "ACTIVE"; // ACTIVE, COMPLETED, ARCHIVED, DELETED

  @Column(name = "due_date")
  private LocalDate dueDate;

  @Column(name = "tags", columnDefinition = "varchar[]")
  private String[] tags;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
