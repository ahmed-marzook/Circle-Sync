import { ipcMain } from 'electron'
import { getDatabase } from '../database.js'
import {
  CarCreateInput,
  CarUpdateInput,
  IpcResponse,
  Car,
  CarCreateInputSchema,
  CarUpdateInputSchema,
  CarSchema,
} from '../../shared/index.js'

/**
 * Register all IPC handlers for car operations
 * These handlers provide CRUD functionality for the cars table
 */
export function registerCarHandlers() {
  const db = getDatabase()

  /**
   * Get all cars from the database
   * Returns cars ordered by creation date (newest first)
   */
  ipcMain.handle('car:getAll', (): IpcResponse<Car[]> => {
    try {
      const rawCars = db
        .prepare('SELECT * FROM cars ORDER BY created_at DESC')
        .all()

      // Validate database response
      const validationResult = CarSchema.array().safeParse(rawCars)
      if (!validationResult.success) {
        console.error('Database validation error:', validationResult.error)
        return {
          success: false,
          error: `Invalid data from database: ${validationResult.error.message}`,
        }
      }

      return { success: true, data: validationResult.data }
    } catch (error) {
      console.error('Error fetching cars:', error)
      return { success: false, error: (error as Error).message }
    }
  })

  /**
   * Get a single car by ID
   */
  ipcMain.handle(
    'car:getById',
    (_event: unknown, id: number): IpcResponse<Car> => {
      try {
        // Validate input ID
        if (!Number.isInteger(id) || id <= 0) {
          return {
            success: false,
            error: 'Invalid car ID: must be a positive integer',
          }
        }

        const rawCar = db.prepare('SELECT * FROM cars WHERE id = ?').get(id)

        if (!rawCar) {
          return { success: false, error: `Car with ID ${id} not found` }
        }

        // Validate database response
        const validationResult = CarSchema.safeParse(rawCar)
        if (!validationResult.success) {
          console.error('Database validation error:', validationResult.error)
          return {
            success: false,
            error: `Invalid data from database: ${validationResult.error.message}`,
          }
        }

        return { success: true, data: validationResult.data }
      } catch (error) {
        console.error('Error fetching car:', error)
        return { success: false, error: (error as Error).message }
      }
    },
  )

  /**
   * Create a new car
   * Accepts car data and returns the created car with its ID
   */
  ipcMain.handle(
    'car:create',
    (_event: unknown, carData: CarCreateInput): IpcResponse<Car> => {
      try {
        // Validate input data
        const validationResult = CarCreateInputSchema.safeParse(carData)
        if (!validationResult.success) {
          console.error('Input validation error:', validationResult.error)
          return {
            success: false,
            error: `Invalid car data: ${validationResult.error.issues.map((e) => e.message).join(', ')}`,
          }
        }

        const validatedData = validationResult.data

        const stmt = db.prepare(
          'INSERT INTO cars (make, model, year, color, vin, mileage) VALUES (@make, @model, @year, @color, @vin, @mileage)',
        )
        const info = stmt.run(validatedData)
        const rawNewCar = db
          .prepare('SELECT * FROM cars WHERE id = ?')
          .get(info.lastInsertRowid)

        // Validate database response
        const carValidation = CarSchema.safeParse(rawNewCar)
        if (!carValidation.success) {
          console.error('Database validation error:', carValidation.error)
          return {
            success: false,
            error: `Invalid data from database: ${carValidation.error.message}`,
          }
        }

        return { success: true, data: carValidation.data }
      } catch (error) {
        console.error('Error creating car:', error)
        return { success: false, error: (error as Error).message }
      }
    },
  )

  /**
   * Update an existing car
   * Accepts car ID and updated data
   */
  ipcMain.handle(
    'car:update',
    (
      _event: unknown,
      id: number,
      carData: CarUpdateInput,
    ): IpcResponse<Car> => {
      try {
        // Validate input ID
        if (!Number.isInteger(id) || id <= 0) {
          return {
            success: false,
            error: 'Invalid car ID: must be a positive integer',
          }
        }

        // Validate input data
        const validationResult = CarUpdateInputSchema.safeParse(carData)
        if (!validationResult.success) {
          console.error('Input validation error:', validationResult.error)
          return {
            success: false,
            error: `Invalid car data: ${validationResult.error.issues.map((e) => e.message).join(', ')}`,
          }
        }

        const validatedData = validationResult.data

        // Build dynamic UPDATE query based on provided fields
        const fieldsToUpdate = Object.keys(validatedData).filter(
          (key) => validatedData[key as keyof CarUpdateInput] !== undefined,
        )

        if (fieldsToUpdate.length === 0) {
          return { success: false, error: 'No fields to update' }
        }

        const updateClause = fieldsToUpdate
          .map((field) => `${field} = @${field}`)
          .join(', ')
        const stmt = db.prepare(
          `UPDATE cars SET ${updateClause} WHERE id = @id`,
        )
        stmt.run({ ...validatedData, id })

        const rawUpdatedCar = db
          .prepare('SELECT * FROM cars WHERE id = ?')
          .get(id)

        if (!rawUpdatedCar) {
          return { success: false, error: `Car with ID ${id} not found` }
        }

        // Validate database response
        const carValidation = CarSchema.safeParse(rawUpdatedCar)
        if (!carValidation.success) {
          console.error('Database validation error:', carValidation.error)
          return {
            success: false,
            error: `Invalid data from database: ${carValidation.error.message}`,
          }
        }

        return { success: true, data: carValidation.data }
      } catch (error) {
        console.error('Error updating car:', error)
        return { success: false, error: (error as Error).message }
      }
    },
  )

  /**
   * Delete a car by ID
   */
  ipcMain.handle(
    'car:delete',
    (_event: unknown, id: number): IpcResponse<void> => {
      try {
        // Validate input ID
        if (!Number.isInteger(id) || id <= 0) {
          return {
            success: false,
            error: 'Invalid car ID: must be a positive integer',
          }
        }

        // Check if car exists before deleting
        const existingCar = db
          .prepare('SELECT id FROM cars WHERE id = ?')
          .get(id)
        if (!existingCar) {
          return { success: false, error: `Car with ID ${id} not found` }
        }

        const stmt = db.prepare('DELETE FROM cars WHERE id = ?')
        stmt.run(id)
        return { success: true }
      } catch (error) {
        console.error('Error deleting car:', error)
        return { success: false, error: (error as Error).message }
      }
    },
  )

  console.log('Car IPC handlers registered')
}
