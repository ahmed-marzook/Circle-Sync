import { ElectronAPI } from '@/shared'

declare global {
  interface Window {
    electronAPI: ElectronAPI
  }
}

// This export makes it a module, which is required for global augmentation
export {}
