import { contextBridge, ipcRenderer } from 'electron'
import {
  ElectronAPI,
  CarCreateInputSchema,
  CarUpdateInputSchema,
  IpcResponse,
  Car,
} from '../shared/index.js'

/**
 * Preload script for secure IPC communication
 *
 * This script runs in an isolated context and exposes a safe API
 * to the renderer process via contextBridge. This prevents the
 * renderer from having direct access to Node.js or Electron APIs.
 */

// Expose the API to the renderer process
contextBridge.exposeInMainWorld('electronAPI', {
  car: {
    getAll: () => ipcRenderer.invoke('car:getAll'),

    getById: (id: number) => {
      // Validate ID before sending to main process
      if (!Number.isInteger(id) || id <= 0) {
        return Promise.resolve({
          success: false,
          error: 'Invalid car ID: must be a positive integer',
        } as IpcResponse<Car>)
      }
      return ipcRenderer.invoke('car:getById', id)
    },

    create: (carData: any) => {
      // Validate car data before sending to main process
      const validationResult = CarCreateInputSchema.safeParse(carData)
      if (!validationResult.success) {
        return Promise.resolve({
          success: false,
          error: `Invalid car data: ${validationResult.error.issues.map((e) => e.message).join(', ')}`,
        } as IpcResponse<Car>)
      }
      return ipcRenderer.invoke('car:create', validationResult.data)
    },

    update: (id: number, carData: any) => {
      // Validate ID
      if (!Number.isInteger(id) || id <= 0) {
        return Promise.resolve({
          success: false,
          error: 'Invalid car ID: must be a positive integer',
        } as IpcResponse<Car>)
      }

      // Validate car data before sending to main process
      const validationResult = CarUpdateInputSchema.safeParse(carData)
      if (!validationResult.success) {
        return Promise.resolve({
          success: false,
          error: `Invalid car data: ${validationResult.error.issues.map((e) => e.message).join(', ')}`,
        } as IpcResponse<Car>)
      }
      return ipcRenderer.invoke('car:update', id, validationResult.data)
    },

    delete: (id: number) => {
      // Validate ID before sending to main process
      if (!Number.isInteger(id) || id <= 0) {
        return Promise.resolve({
          success: false,
          error: 'Invalid car ID: must be a positive integer',
        } as IpcResponse<void>)
      }
      return ipcRenderer.invoke('car:delete', id)
    },
  },
} as ElectronAPI)
