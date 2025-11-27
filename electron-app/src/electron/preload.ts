import { contextBridge, ipcRenderer } from 'electron'

/**
 * Preload script for secure IPC communication
 *
 * This script runs in an isolated context and exposes a safe API
 * to the renderer process via contextBridge. This prevents the
 * renderer from having direct access to Node.js or Electron APIs.
 */

// Define the API interface that will be exposed to the renderer
export interface ElectronAPI {
  car: {
    getAll: () => Promise<any>
    getById: (id: number) => Promise<any>
    create: (carData: any) => Promise<any>
    update: (id: number, carData: any) => Promise<any>
    delete: (id: number) => Promise<any>
  }
}

// Expose the API to the renderer process
contextBridge.exposeInMainWorld('electronAPI', {
  car: {
    getAll: () => ipcRenderer.invoke('car:getAll'),
    getById: (id: number) => ipcRenderer.invoke('car:getById', id),
    create: (carData: any) => ipcRenderer.invoke('car:create', carData),
    update: (id: number, carData: any) => ipcRenderer.invoke('car:update', id, carData),
    delete: (id: number) => ipcRenderer.invoke('car:delete', id),
  },
} as ElectronAPI)
