# Circle-Sync Module Separation - Quick Reference

## File Locations

### Circle Module Root
`/home/user/Circle-Sync/circlesync/src/main/java/com/circlesync/circlesync/circlemodule/`

**Key Files**:
- `Circle.java` - Module marker (Spring Modulith)
- `entity/Circle.java` - Main Circle entity
- `entity/CircleMember.java` - Member association entity
- `service/CircleService.java` - Service interface
- `service/CircleServiceImpl.java` - Implementation (517 lines)
- `controller/CircleController.java` - REST endpoints
- `config/CircleDatabaseConfig.java` - Database configuration
- `repository/CircleRepository.java` - Data access
- `mapper/CircleMapper.java`, `MemberMapper.java` - DTOs
- `exception/` - 7 exception classes

### Task Module Root
`/home/user/Circle-Sync/circlesync/src/main/java/com/circlesync/circlesync/taskmodule/`

**Key Files**:
- `Task.java` - Module marker (Spring Modulith)
- `entity/Task.java` - Main Task entity
- `entity/TaskCompletion.java` - Completion tracking
- `entity/Streak.java` - Streak tracking
- `service/TaskService.java` - Service interface
- `service/TaskServiceImpl.java` - Implementation (268 lines)
- `controller/TaskController.java` - REST endpoints (8 endpoints)
- `config/TaskDatabaseConfig.java` - Database configuration
- `repository/TaskRepository.java` - Data access
- `mapper/` - 3 mapper classes
- `exception/TaskNotFoundException.java` - Single exception

## Data Models at a Glance

### Circle DB Schema
```
circles (8 indexes)
├── id: UUID (PK)
├── name, description, circle_type
├── privacy, invite_code (UNIQUE)
├── avatar_url, settings (JSONB)
├── created_by, created_at, updated_at
└── Constraints: CHECK(circle_type), CHECK(privacy)

circle_members (7 indexes)
├── id: UUID (PK)
├── circle_id: UUID (FK → circles CASCADE)
├── user_id, user_name, user_avatar
├── role (ADMIN|MEMBER|VIEWER)
├── nickname, joined_at, updated_at
└── Constraints: UNIQUE(circle_id, user_id)
```

### Task DB Schema
```
tasks (7 indexes)
├── id: UUID (PK)
├── circle_id: UUID (NO FK ⚠️ - DIFFERENT DATABASE)
├── created_by, assigned_to (UUID[])
├── title, description, type (HABIT|TODO)
├── category, frequency, visibility
├── points, status, due_date, tags
└── Constraints: CHECK(type), CHECK(status), CHECK(visibility)

task_completions (4 indexes)
├── id: UUID (PK)
├── task_id: UUID (FK → tasks CASCADE)
├── user_id, completed_at, date
└── notes

streaks (3 indexes)
├── id: UUID (PK)
├── task_id: UUID (FK → tasks CASCADE)
├── user_id, current_streak, longest_streak
└── last_completed_date, updated_at
```

## Dependencies Summary

### Import Dependencies
```
Circle Module imports:
  - spring.*, jakarta.*, lombok.*
  - Only circlemodule.* packages
  - NO taskmodule imports ✅

Task Module imports:
  - spring.*, jakarta.*, lombok.*
  - Only taskmodule.* packages
  - NO circlemodule imports ✅

Cross-module imports: ZERO ✅
```

### Data Dependencies
```
Circle → Task: NONE ✅

Task → Circle: EXISTS ⚠️
  - Task.circleId field
  - TaskRepository.findByCircleId()
  - TaskRepository.deleteByCircleId()
  - But NO validation or cascade at application level
```

## API Endpoints Summary

### Circle Module (40+ endpoints)
```
/api/circles                           - Circle CRUD
/api/circles/{id}                      - Details, Update, Delete
/api/circles/user/{userId}             - User's circles
/api/circles/search                    - Search with filters
/api/circles/join/{code}               - Join by invite
/api/circles/{id}/members              - Member management
/api/circles/{id}/stats                - Statistics
```

### Task Module (8 endpoints)
```
/api/tasks                             - Task CRUD
/api/tasks/{id}                        - Details, Update, Delete
/api/tasks/circle/{id}                 - Circle's tasks
/api/tasks/{id}/complete               - Mark complete
/api/tasks/{id}/completions            - Completion history
/api/tasks/{id}/streak/user/{userId}   - Streak info
```

## Configuration Files

### Databases
```yaml
spring:
  datasource:
    circle:
      jdbc-url: jdbc:postgresql://localhost:5432/circles_db
      username: circles_user
      
    task:
      jdbc-url: jdbc:postgresql://localhost:5432/tasks_db
      username: tasks_user
```

### Docker Compose Services
```yaml
- postgres:5432 (Single instance with 3 databases)
- kafka:9092
- schema-registry:9094
- kafka-ui:8081 (http://localhost:8081)
```

## Critical Issues

### Data Consistency Problems
1. **Orphaned Tasks**: Circle deletion doesn't cascade to tasks (different DB)
2. **No Validation**: Tasks can be created with invalid circle_id
3. **No Transactions**: Cross-module operations not atomic
4. **No Cascade**: TaskRepository.deleteByCircleId() not called by CircleService

### Architectural Gaps
1. **No Event-Based Communication**: Kafka configured but unused
2. **Incomplete Exception Handling**: Task module uses Circle's handler
3. **No User Context**: getCurrentUserId() returns random UUID
4. **No Inter-Module API**: No REST calls between modules (currently)

## Module Isolation Score

| Category | Score | Status |
|----------|-------|--------|
| Code Separation | 10/10 | ✅ Perfect - No cross-module imports |
| Database Separation | 9/10 | ✅ Good - Independent databases |
| Service Isolation | 9/10 | ✅ Good - Services independent |
| Data Consistency | 2/10 | ❌ Poor - No validation/enforcement |
| Exception Handling | 6/10 | ⚠️ Partial - Shared handler |
| API Separation | 10/10 | ✅ Perfect - Independent endpoints |
| Inter-Module Comm | 1/10 | ❌ Missing - No event infrastructure |
| Transaction Safety | 3/10 | ❌ Poor - No distributed transactions |
| **Overall** | **6.3/10** | ⚠️ Good code separation, weak data coupling |

## What Works Well
✅ Clean package structure
✅ No code duplication
✅ Independent REST APIs
✅ Spring Modulith module markers
✅ Separate database configurations
✅ Comprehensive validation
✅ Good service design
✅ MapStruct for mapping

## What Needs Work
❌ Enforce circle_id validation
❌ Implement cascade delete for tasks
❌ Add GlobalExceptionHandler to Task module
❌ Implement event-based communication
❌ Fix getCurrentUserId() placeholder
❌ Add distributed transaction support
❌ Implement Kafka messaging
❌ Add contract-based API testing

## Next Steps for Better Separation

### Immediate (1-2 sprints)
1. Add circle validation in TaskServiceImpl.createTask()
2. Create Task module GlobalExceptionHandler
3. Add cascade delete logic when circle deleted
4. Implement circleId validation in repository queries

### Short-term (2-4 sprints)
1. Publish CircleDeletedEvent when circle deleted
2. Subscribe to event in Task module for cleanup
3. Validate circle exists before task creation
4. Implement Saga pattern for distributed transactions

### Medium-term (4-8 sprints)
1. Separate into independent deployment units
2. Implement REST client between modules
3. Add contract-based API testing
4. Implement CQRS patterns if needed

### Long-term
1. Fully event-driven architecture
2. True microservices separation
3. Event sourcing for audit trails
4. Independent scaling and deployment

## File Locations Summary

```
Total Java Files: 46
- Circle Module: 25 files
- Task Module: 16 files
- Main/Config: 5 files

Databases: 2 (circles_db, tasks_db)
Tables: 5 (circles, circle_members, tasks, task_completions, streaks)
Indexes: 18 total
Triggers: 4 (auto-update timestamps)
```

