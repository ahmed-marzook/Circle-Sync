package com.circlesync.circlesync.taskmodule.mapper;

import com.circlesync.circlesync.taskmodule.dto.CompleteTaskRequest;
import com.circlesync.circlesync.taskmodule.dto.TaskCompletionResponse;
import com.circlesync.circlesync.taskmodule.entity.TaskCompletion;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for TaskCompletion entity.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskCompletionMapper {

  /**
   * Convert CompleteTaskRequest to TaskCompletion entity.
   *
   * @param request the complete task request
   * @param taskId the task ID
   * @return the task completion entity
   */
  @Mapping(target = "taskId", source = "taskId")
  @Mapping(target = "userId", source = "request.userId")
  @Mapping(target = "notes", source = "request.notes")
  @Mapping(target = "date", source = "request.date")
  TaskCompletion toEntity(CompleteTaskRequest request, UUID taskId);

  /**
   * Convert TaskCompletion entity to TaskCompletionResponse.
   *
   * @param taskCompletion the task completion entity
   * @return the task completion response
   */
  TaskCompletionResponse toResponse(TaskCompletion taskCompletion);
}
