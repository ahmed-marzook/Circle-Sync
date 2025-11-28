/**
 * IPC Response-related Zod schemas for runtime validation
 * These schemas validate IPC responses for communication between main and renderer processes
 */

import { z } from 'zod'
import { CarSchema } from './car.schemas.js'

/**
 * IPC Response Schema
 */

// Generic IPC response wrapper
export const IpcResponseSchema = <T extends z.ZodTypeAny>(dataSchema: T) =>
  z.object({
    success: z.boolean(),
    data: dataSchema.optional(),
    error: z.string().optional(),
  })

// Specific IPC response schemas for car operations
export const CarIpcResponseSchema = IpcResponseSchema(CarSchema)
export const CarArrayIpcResponseSchema = IpcResponseSchema(z.array(CarSchema))
export const VoidIpcResponseSchema = IpcResponseSchema(z.void())

/**
 * Type exports inferred from schemas
 * These provide TypeScript types that match the validated runtime types
 */

export type IpcResponseSchemaType<T> = {
  success: boolean
  data?: T
  error?: string
}
