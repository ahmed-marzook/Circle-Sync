/**
 * Shared IPC types used by both main and renderer processes
 */

/**
 * Shared IPC types used by both main and renderer processes
 */
import type { Car, CarCreateInput, CarUpdateInput } from './car.js'

/**
 * Standard IPC response wrapper
 * All IPC handlers should return this format for consistent error handling
 */
export interface IpcResponse<T = any> {
  success: boolean
  data?: T
  error?: string
}

/**
 * ElectronAPI interface exposed to the renderer process via contextBridge
 * This defines the secure API that the renderer can use to communicate with the main process
 */
export interface ElectronAPI {
  car: {
    getAll: () => Promise<IpcResponse<Car[]>>
    getById: (id: number) => Promise<IpcResponse<Car>>
    create: (carData: CarCreateInput) => Promise<IpcResponse<Car>>
    update: (id: number, carData: CarUpdateInput) => Promise<IpcResponse<Car>>
    delete: (id: number) => Promise<IpcResponse<void>>
  }
}
