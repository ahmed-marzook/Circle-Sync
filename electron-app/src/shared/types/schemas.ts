/**
 * Zod schemas barrel export
 * This file re-exports all schemas from domain-specific files for backward compatibility
 *
 * For new code, prefer importing directly from domain-specific schema files:
 * - import { CarSchema } from './car.schemas.js'
 * - import { CircleSchema } from './circle.schemas.js'
 * - import { IpcResponseSchema } from './ipc.schemas.js'
 */

// Re-export car schemas
export * from './car.schemas.js'

// Re-export circle and member schemas
export * from './circle.schemas.js'

// Re-export IPC response schemas
export * from './ipc.schemas.js'
