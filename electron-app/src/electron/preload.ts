import { contextBridge, ipcRenderer } from 'electron'
import { ElectronAPI } from '../shared/index.js'

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
    getById: (id: number) => ipcRenderer.invoke('car:getById', id),
    create: (carData: any) => ipcRenderer.invoke('car:create', carData),
    update: (id: number, carData: any) => ipcRenderer.invoke('car:update', id, carData),
    delete: (id: number) => ipcRenderer.invoke('car:delete', id),
  },
} as ElectronAPI)
