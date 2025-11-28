/**
 * Shared Car types used by both main and renderer processes
 */

export interface Car {
  id: number
  make: string
  model: string
  year: number
  color?: string
  vin?: string
  mileage?: number
  created_at?: string
}

export interface CarCreateInput {
  make: string
  model: string
  year: number
  color?: string
  vin?: string
  mileage?: number
}

export interface CarUpdateInput {
  make?: string
  model?: string
  year?: number
  color?: string
  vin?: string
  mileage?: number
}
