import { app, BrowserWindow } from 'electron'
import path from 'node:path'
import fs from 'node:fs'
import { isDev } from './util.js'
import Database from 'better-sqlite3'

// Better path management
function getDatabasePath(): string {
  if (isDev()) {
    // Dev: Store in project root
    return 'circle-sync.db'
  } else {
    // Production: Store in user data directory
    const userDataPath = app.getPath('userData')

    // Ensure directory exists
    if (!fs.existsSync(userDataPath)) {
      fs.mkdirSync(userDataPath, { recursive: true })
    }

    return path.join(userDataPath, 'circle-sync.db')
  }
}

// Move DB initialization into app.whenReady() to ensure app is ready
let db: Database.Database

// Initialize database schema
function initDatabase() {
  // Create table if it doesn't exist
  db.exec(`
    CREATE TABLE IF NOT EXISTS cats (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT NOT NULL,
      age INTEGER NOT NULL
    )
  `)
}

function createWindow() {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false,
      // preload: path.join(__dirname, 'preload.js')
    },
  })

  if (isDev()) {
    win.loadURL('http://localhost:5123')
  } else {
    win.loadFile(path.join(app.getAppPath(), '/dist-react/index.html'))
  }
}

app.whenReady().then(() => {
  // Initialize DB after app is ready
  const dbPath = getDatabasePath()
  console.log('Database location:', dbPath)

  db = new Database(dbPath, { verbose: console.log })

  // Initialize database first
  initDatabase()

  // Check if cats table already has data
  const count = db.prepare('SELECT COUNT(*) as count FROM cats').get() as {
    count: number
  }

  if (count.count === 0) {
    // Only insert if table is empty
    const insert = db.prepare(
      'INSERT INTO cats (name, age) VALUES (@name, @age)',
    )

    const insertMany = db.transaction((cats) => {
      for (const cat of cats) insert.run(cat)
    })

    insertMany([
      { name: 'Joey', age: 2 },
      { name: 'Sally', age: 4 },
      { name: 'Junior', age: 1 },
    ])

    console.log('Sample data inserted')
  }

  // Query to verify
  const cats = db.prepare('SELECT * FROM cats').all()
  console.log('Current cats in database:', cats)

  createWindow()

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow()
    }
  })
})

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    try {
      if (db) {
        db.close()
        console.log('Database connection closed')
      }
    } catch (error) {
      console.error('Error closing database:', error)
    }
    app.quit()
  }
})

// Close database on app quit
app.on('quit', () => {
  try {
    if (db) {
      db.close()
      console.log('Database connection closed')
    }
  } catch (error) {
    console.error('Error closing database:', error)
  }
})
