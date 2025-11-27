// Car service for Electron IPC communication with the main process
// This service provides a clean API for the React UI to interact with the car database

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

interface IpcResponse<T = any> {
  success: boolean
  data?: T
  error?: string
}

// Type-safe wrapper for IPC calls
const ipcInvoke = async <T = any>(
  channel: string,
  ...args: any[]
): Promise<IpcResponse<T>> => {
  // Since nodeIntegration is true, we can access electron directly
  const { ipcRenderer } = window.require('electron')
  return ipcRenderer.invoke(channel, ...args)
}

export class CarService {
  /**
   * Get all cars from the database
   */
  async getAllCars(): Promise<Car[]> {
    try {
      const response = await ipcInvoke<Car[]>('car:getAll')
      if (response.success && response.data) {
        return response.data
      }
      console.error('[CarService] Failed to fetch cars:', response.error)
      return []
    } catch (error) {
      console.error('[CarService] Error fetching cars:', error)
      return []
    }
  }

  /**
   * Get a single car by ID
   */
  async getCarById(id: number): Promise<Car | null> {
    try {
      const response = await ipcInvoke<Car>('car:getById', id)
      if (response.success && response.data) {
        return response.data
      }
      console.error('[CarService] Failed to fetch car:', response.error)
      return null
    } catch (error) {
      console.error('[CarService] Error fetching car:', error)
      return null
    }
  }

  /**
   * Create a new car
   */
  async createCar(carData: CarCreateInput): Promise<Car | null> {
    try {
      const response = await ipcInvoke<Car>('car:create', carData)
      if (response.success && response.data) {
        return response.data
      }
      console.error('[CarService] Failed to create car:', response.error)
      return null
    } catch (error) {
      console.error('[CarService] Error creating car:', error)
      return null
    }
  }

  /**
   * Update an existing car
   */
  async updateCar(id: number, carData: CarUpdateInput): Promise<Car | null> {
    try {
      const response = await ipcInvoke<Car>('car:update', id, carData)
      if (response.success && response.data) {
        return response.data
      }
      console.error('[CarService] Failed to update car:', response.error)
      return null
    } catch (error) {
      console.error('[CarService] Error updating car:', error)
      return null
    }
  }

  /**
   * Delete a car
   */
  async deleteCar(id: number): Promise<boolean> {
    try {
      const response = await ipcInvoke('car:delete', id)
      return response.success
    } catch (error) {
      console.error('[CarService] Error deleting car:', error)
      return false
    }
  }

  /**
   * Seed a random car (for testing/development)
   */
  seedOne(): CarCreateInput {
    const makes = ['Toyota', 'Honda', 'Ford', 'Tesla', 'BMW', 'Mercedes']
    const models = ['Sedan', 'SUV', 'Truck', 'Coupe', 'Hatchback']
    const colors = ['Red', 'Blue', 'Black', 'White', 'Silver', 'Gray']

    return {
      make: makes[Math.floor(Math.random() * makes.length)],
      model: models[Math.floor(Math.random() * models.length)],
      year: 2018 + Math.floor(Math.random() * 6), // 2018-2023
      color: colors[Math.floor(Math.random() * colors.length)],
      vin: `VIN${Math.random().toString(36).substring(2, 15).toUpperCase()}`,
      mileage: Math.floor(Math.random() * 100000),
    }
  }
}

// Export a singleton instance
export const carService = new CarService()

// Quick usage example:
// import { carService } from './services/api/carService';
// const cars = await carService.getAllCars();
// const newCar = await carService.createCar({ make: 'Toyota', model: 'Camry', year: 2023 });
