/**
 * Shared IPC types used by both main and renderer processes
 */

/**
 * Shared IPC types used by both main and renderer processes
 */
import type {
  Car,
  CarCreateInput,
  CarUpdateInput,
} from '../../shared/types/car.js'

/**
 * Standard IPC response wrapper
 * All IPC handlers should return this format for consistent error handling
 */
export interface IpcResponse<T = any> {
  success: boolean
  data?: T
  error?: string
}

export type FrameWindowAction = 'CLOSE' | 'MAXIMIZE' | 'MINIMIZE'

/**
 * EventPayloadMapping defines all IPC channels and their payload types
 * This provides type safety for IPC communication between main and renderer processes
 */
export interface EventPayloadMapping {
  // Car operations (invoke - request/response)
  'car:getAll': IpcResponse<Car[]>
  'car:getById': IpcResponse<Car>
  'car:create': IpcResponse<Car>
  'car:update': IpcResponse<Car>
  'car:delete': IpcResponse<void>

  // Car events (on - one-way from main to renderer)
  'car:added': Car
  'car:updated': Car
  'car:deleted': number // Just the ID of the deleted car
}

export type UnsubscribeFunction = () => void

/**
 * ElectronAPI interface exposed to the renderer process via contextBridge
 * This defines the secure API that the renderer can use to communicate with the main process
 */
export interface ElectronAPI {
  car: {
    // Request/Response operations (invoke)
    getAll: () => Promise<IpcResponse<Car[]>>
    getById: (id: number) => Promise<IpcResponse<Car>>
    create: (carData: CarCreateInput) => Promise<IpcResponse<Car>>
    update: (id: number, carData: CarUpdateInput) => Promise<IpcResponse<Car>>
    delete: (id: number) => Promise<IpcResponse<void>>

    // Event subscriptions (on) - returns cleanup function
    onCarAdded: (callback: (car: Car) => void) => () => void
    onCarUpdated: (callback: (car: Car) => void) => () => void
    onCarDeleted: (callback: (id: number) => void) => () => void
  }
}
