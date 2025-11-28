/**
 * Window type declarations for Electron API
 * Extends the Window interface to include the electronAPI exposed via contextBridge
 */

import type { ElectronAPI } from '@/shared'

declare global {
  interface Window {
    electronAPI: ElectronAPI
  }
}

export {}
