import { app, BrowserWindow } from 'electron'
import path from 'node:path'
import { isDev } from './util.js'
import { fileURLToPath } from 'node:url'
import { initDatabase, closeDatabase } from './database.js'
import { registerCarHandlers } from './ipc/carHandlers.js'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

function createWindow() {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload.js'),
    },
  })

  if (isDev()) {
    win.loadURL('http://localhost:5123')
  } else {
    win.loadFile(path.join(app.getAppPath(), '/dist-react/index.html'))
  }
}

app.whenReady().then(() => {
  // Initialize database (creates tables and seeds data)
  initDatabase()

  // Register IPC handlers for car operations
  registerCarHandlers()

  // Create the main window
  createWindow()

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow()
    }
  })
})

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    closeDatabase()
    app.quit()
  }
})

// Close database on app quit
app.on('quit', () => {
  closeDatabase()
})
