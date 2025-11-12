package com.circlesync.circlesync.taskmodule.mapper;

import com.circlesync.circlesync.taskmodule.dto.CreateTaskRequest;
import com.circlesync.circlesync.taskmodule.dto.TaskResponse;
import com.circlesync.circlesync.taskmodule.dto.UpdateTaskRequest;
import com.circlesync.circlesync.taskmodule.entity.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Task entity.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {

  /**
   * Convert CreateTaskRequest to Task entity.
   *
   * @param request the create task request
   * @return the task entity
   */
  Task toEntity(CreateTaskRequest request);

  /**
   * Convert Task entity to TaskResponse.
   *
   * @param task the task entity
   * @return the task response
   */
  TaskResponse toResponse(Task task);

  /**
   * Update Task entity from UpdateTaskRequest (full update).
   *
   * @param request the update request
   * @param task the task entity to update
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
  void updateEntityFromRequest(UpdateTaskRequest request, @MappingTarget Task task);

  /**
   * Partially update Task entity from UpdateTaskRequest (patch update). Null values are ignored.
   *
   * @param request the update request
   * @param task the task entity to update
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntityFromRequest(UpdateTaskRequest request, @MappingTarget Task task);
}
