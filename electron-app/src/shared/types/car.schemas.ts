/**
 * Car-related Zod schemas for runtime validation
 * These schemas validate car data at IPC boundaries and API responses
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
 * Type exports inferred from schemas
 * These provide TypeScript types that match the validated runtime types
 */

export type CarSchemaType = z.infer<typeof CarSchema>
export type CarCreateInputSchemaType = z.infer<typeof CarCreateInputSchema>
export type CarUpdateInputSchemaType = z.infer<typeof CarUpdateInputSchema>
