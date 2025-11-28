import { contextBridge, ipcRenderer } from 'electron'
import { ElectronAPI, EventPayloadMapping } from '../shared/index.js'

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
    update: (id: number, carData: any) =>
      ipcRenderer.invoke('car:update', id, carData),
    delete: (id: number) => ipcRenderer.invoke('car:delete', id),
  },
} as ElectronAPI)

function ipcInvoke<Key extends keyof EventPayloadMapping>(
  key: Key,
): Promise<EventPayloadMapping[Key]> {
  return ipcRenderer.invoke(key)
}

function ipcOn<Key extends keyof EventPayloadMapping>(
  key: Key,
  callback: (payload: EventPayloadMapping[Key]) => void,
) {
  const cb = (_: Electron.IpcRendererEvent, payload: any) => callback(payload)
  ipcRenderer.on(key, cb)
  return () => ipcRenderer.off(key, cb)
}

function ipcSend<Key extends keyof EventPayloadMapping>(
  key: Key,
  payload: EventPayloadMapping[Key],
) {
  ipcRenderer.send(key, payload)
}
