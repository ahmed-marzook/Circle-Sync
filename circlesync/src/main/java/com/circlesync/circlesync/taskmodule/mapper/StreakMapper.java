package com.circlesync.circlesync.taskmodule.mapper;

import com.circlesync.circlesync.taskmodule.dto.StreakResponse;
import com.circlesync.circlesync.taskmodule.entity.Streak;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Streak entity.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StreakMapper {

  /**
   * Convert Streak entity to StreakResponse.
   *
   * @param streak the streak entity
   * @return the streak response
   */
  StreakResponse toResponse(Streak streak);
}
