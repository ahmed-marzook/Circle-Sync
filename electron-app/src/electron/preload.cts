import { contextBridge, ipcRenderer } from 'electron'
import { ElectronAPI, EventPayloadMapping } from './ipc/ipc.types'

/**
 * Preload script for secure IPC communication
 *
 * This script runs in an isolated context and exposes a safe API
 * to the renderer process via contextBridge. This prevents the
 * renderer from having direct access to Node.js or Electron APIs.
 */

/**
 * Helper function for request/response IPC with parameters (invoke)
 * Used for: CRUD operations, fetching data
 */
function ipcInvoke<Key extends keyof EventPayloadMapping>(
  key: Key,
  ...args: any[]
): Promise<EventPayloadMapping[Key]> {
  return ipcRenderer.invoke(key, ...args)
}

/**
 * Helper function for subscribing to events (on)
 * Used for: Real-time updates, notifications from main process
 * Returns cleanup function to unsubscribe
 */
function ipcOn<Key extends keyof EventPayloadMapping>(
  key: Key,
  callback: (payload: EventPayloadMapping[Key]) => void,
) {
  const cb = (_: Electron.IpcRendererEvent, payload: any) => callback(payload)
  ipcRenderer.on(key, cb)
  return () => ipcRenderer.off(key, cb)
}

/**
 * Helper function for fire-and-forget IPC (send)
 * Used for: Commands, actions that don't need a response
 */
function ipcSend<Key extends keyof EventPayloadMapping>(
  key: Key,
  payload: EventPayloadMapping[Key],
) {
  ipcRenderer.send(key, payload)
}

// Expose the API to the renderer process
contextBridge.exposeInMainWorld('electronAPI', {
  car: {
    // Request/Response operations (invoke) - NOW WITH PARAMETERS!
    getAll: () => ipcRenderer.invoke('car:getAll'),
    getById: (id: number) => ipcRenderer.invoke('car:getById', id),
    create: (carData: any) => ipcRenderer.invoke('car:create', carData),
    update: (id: number, carData: any) =>
      ipcRenderer.invoke('car:update', id, carData),
    delete: (id: number) => ipcRenderer.invoke('car:delete', id),

    // Event subscriptions (on) - for real-time updates
    onCarAdded: (callback: (car: any) => void) => ipcOn('car:added', callback),
    onCarUpdated: (callback: (car: any) => void) =>
      ipcOn('car:updated', callback),
    onCarDeleted: (callback: (id: number) => void) =>
      ipcOn('car:deleted', callback),
  },
} as ElectronAPI)
