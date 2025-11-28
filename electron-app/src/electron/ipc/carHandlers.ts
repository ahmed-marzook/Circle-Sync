import { ipcMain } from 'electron'
import { getDatabase } from '../database.js'
import { CarCreateInput, CarUpdateInput, IpcResponse, Car } from '../../shared/index.js'

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
      const cars = db.prepare('SELECT * FROM cars ORDER BY created_at DESC').all() as Car[]
      return { success: true, data: cars }
    } catch (error) {
      console.error('Error fetching cars:', error)
      return { success: false, error: (error as Error).message }
    }
  })

  /**
   * Get a single car by ID
   */
  ipcMain.handle('car:getById', (_event, id: number): IpcResponse<Car> => {
    try {
      const car = db.prepare('SELECT * FROM cars WHERE id = ?').get(id) as Car
      return { success: true, data: car }
    } catch (error) {
      console.error('Error fetching car:', error)
      return { success: false, error: (error as Error).message }
    }
  })

  /**
   * Create a new car
   * Accepts car data and returns the created car with its ID
   */
  ipcMain.handle('car:create', (_event, carData: CarCreateInput): IpcResponse<Car> => {
    try {
      const stmt = db.prepare(
        'INSERT INTO cars (make, model, year, color, vin, mileage) VALUES (@make, @model, @year, @color, @vin, @mileage)',
      )
      const info = stmt.run(carData)
      const newCar = db.prepare('SELECT * FROM cars WHERE id = ?').get(info.lastInsertRowid) as Car
      return { success: true, data: newCar }
    } catch (error) {
      console.error('Error creating car:', error)
      return { success: false, error: (error as Error).message }
    }
  })

  /**
   * Update an existing car
   * Accepts car ID and updated data
   */
  ipcMain.handle('car:update', (_event, id: number, carData: CarUpdateInput): IpcResponse<Car> => {
    try {
      const stmt = db.prepare(
        'UPDATE cars SET make = @make, model = @model, year = @year, color = @color, vin = @vin, mileage = @mileage WHERE id = @id',
      )
      stmt.run({ ...carData, id })
      const updatedCar = db.prepare('SELECT * FROM cars WHERE id = ?').get(id) as Car
      return { success: true, data: updatedCar }
    } catch (error) {
      console.error('Error updating car:', error)
      return { success: false, error: (error as Error).message }
    }
  })

  /**
   * Delete a car by ID
   */
  ipcMain.handle('car:delete', (_event, id: number): IpcResponse<void> => {
    try {
      const stmt = db.prepare('DELETE FROM cars WHERE id = ?')
      stmt.run(id)
      return { success: true }
    } catch (error) {
      console.error('Error deleting car:', error)
      return { success: false, error: (error as Error).message }
    }
  })

  console.log('Car IPC handlers registered')
}
