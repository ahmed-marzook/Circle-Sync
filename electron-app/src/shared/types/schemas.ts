/**
 * Zod schemas for runtime validation
 * These schemas validate data at IPC boundaries and API responses
 */

import { z } from 'zod'

/**
 * Car Schemas
 */

// Base car schema for validation
export const CarSchema = z.object({
  id: z.number().int().positive(),
  make: z.string().min(1, 'Make is required').max(100),
  model: z.string().min(1, 'Model is required').max(100),
  year: z
    .number()
    .int()
    .min(1900, 'Year must be 1900 or later')
    .max(new Date().getFullYear() + 1, 'Year cannot be in the future'),
  color: z.string().max(50).optional(),
  vin: z
    .string()
    .max(17)
    .regex(/^[A-HJ-NPR-Z0-9]{17}$/, 'VIN must be 17 alphanumeric characters')
    .optional()
    .or(z.literal('')),
  mileage: z.number().int().min(0, 'Mileage cannot be negative').optional(),
  created_at: z.string().datetime().optional(),
})

// Schema for creating a new car (no id or created_at)
export const CarCreateInputSchema = z.object({
  make: z.string().min(1, 'Make is required').max(100),
  model: z.string().min(1, 'Model is required').max(100),
  year: z
    .number()
    .int()
    .min(1900, 'Year must be 1900 or later')
    .max(new Date().getFullYear() + 1, 'Year cannot be in the future'),
  color: z.string().max(50).optional(),
  vin: z
    .string()
    .max(17)
    .regex(/^[A-HJ-NPR-Z0-9]{17}$/, 'VIN must be 17 alphanumeric characters')
    .optional()
    .or(z.literal('')),
  mileage: z.number().int().min(0, 'Mileage cannot be negative').optional(),
})

// Schema for updating a car (all fields optional except at least one must be present)
export const CarUpdateInputSchema = z
  .object({
    make: z.string().min(1, 'Make cannot be empty').max(100).optional(),
    model: z.string().min(1, 'Model cannot be empty').max(100).optional(),
    year: z
      .number()
      .int()
      .min(1900, 'Year must be 1900 or later')
      .max(new Date().getFullYear() + 1, 'Year cannot be in the future')
      .optional(),
    color: z.string().max(50).optional(),
    vin: z
      .string()
      .max(17)
      .regex(/^[A-HJ-NPR-Z0-9]{17}$/, 'VIN must be 17 alphanumeric characters')
      .optional()
      .or(z.literal('')),
    mileage: z.number().int().min(0, 'Mileage cannot be negative').optional(),
  })
  .refine((data: Record<string, unknown>) => Object.keys(data).length > 0, {
    message: 'At least one field must be provided for update',
  })

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
 * Circle and Member Schemas
 */

export const MemberRoleSchema = z.enum(['ADMIN', 'MEMBER', 'VIEWER']).or(z.string())

export const MemberSchema = z.object({
  userId: z.string().min(1, 'User ID is required'),
  userName: z.string().min(1, 'User name is required'),
  userAvatar: z.string().url().optional().or(z.literal('')),
  nickname: z.string().optional(),
  role: MemberRoleSchema.optional(),
})

export const CirclePrivacySchema = z
  .enum(['PUBLIC', 'PRIVATE', 'INVITE_ONLY'])
  .or(z.string())

export const CircleSchema = z.object({
  id: z.string().min(1, 'Circle ID is required'),
  name: z.string().min(1, 'Circle name is required').max(200),
  description: z.string().max(1000).optional(),
  circleType: z.string().optional(),
  privacy: CirclePrivacySchema.optional(),
  avatarUrl: z.string().url().optional().or(z.literal('')),
  settings: z.record(z.string(), z.any()).optional(),
  inviteCode: z.string().optional(),
  members: z.array(MemberSchema).optional(),
  createdAt: z.string().datetime().optional().or(z.string()),
})

// Schema for creating a circle (id and createdAt are generated)
export const CircleCreateInputSchema = z.object({
  name: z.string().min(1, 'Circle name is required').max(200),
  description: z.string().max(1000).optional(),
  circleType: z.string().optional(),
  privacy: CirclePrivacySchema.optional(),
  avatarUrl: z.string().url().optional().or(z.literal('')),
  settings: z.record(z.string(), z.any()).optional(),
  members: z.array(MemberSchema).optional(),
})

/**
 * Type exports inferred from schemas
 * These provide TypeScript types that match the validated runtime types
 */

export type CarSchemaType = z.infer<typeof CarSchema>
export type CarCreateInputSchemaType = z.infer<typeof CarCreateInputSchema>
export type CarUpdateInputSchemaType = z.infer<typeof CarUpdateInputSchema>
export type IpcResponseSchemaType<T> = {
  success: boolean
  data?: T
  error?: string
}
export type MemberSchemaType = z.infer<typeof MemberSchema>
export type CircleSchemaType = z.infer<typeof CircleSchema>
export type CircleCreateInputSchemaType = z.infer<typeof CircleCreateInputSchema>
