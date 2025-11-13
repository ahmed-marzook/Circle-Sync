import { app, BrowserWindow } from 'electron'
import path, { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

type test = string

function createWindow() {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    // webPreferences: {
    //     preload: path.join(__dirname, 'preload.js')
    // }
  })

  win.loadFile(path.join(app.getAppPath(), '/dist-react/index.html'))
}

app.whenReady().then(() => {
  createWindow()

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow()
    }
  })
})

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})
