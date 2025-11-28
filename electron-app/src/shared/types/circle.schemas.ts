/**
 * Circle and Member-related Zod schemas for runtime validation
 * These schemas validate circle and member data at IPC boundaries and API responses
 */

import { z } from 'zod'

/**
 * Member Schemas
 */

export const MemberRoleSchema = z.enum(['ADMIN', 'MEMBER', 'VIEWER']).or(z.string())

export const MemberSchema = z.object({
  userId: z.string().min(1, 'User ID is required'),
  userName: z.string().min(1, 'User name is required'),
  userAvatar: z.string().url().optional().or(z.literal('')),
  nickname: z.string().optional(),
  role: MemberRoleSchema.optional(),
})

/**
 * Circle Schemas
 */

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

export type MemberSchemaType = z.infer<typeof MemberSchema>
export type CircleSchemaType = z.infer<typeof CircleSchema>
export type CircleCreateInputSchemaType = z.infer<typeof CircleCreateInputSchema>
