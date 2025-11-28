/**
 * Shared types barrel export
 * Import all shared types from a single location
 */

export * from './car.js'
export * from './ipc.js'

// Export from domain-specific schema files
export * from './car.schemas.js'
export * from './circle.schemas.js'
export * from './ipc.schemas.js'

// Also maintain backward compatibility with schemas.ts
export * from './schemas.js'
