import { app, BrowserWindow, ipcMain } from 'electron'
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

  // Create cars table
  db.exec(`
    CREATE TABLE IF NOT EXISTS cars (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      make TEXT NOT NULL,
      model TEXT NOT NULL,
      year INTEGER NOT NULL,
      color TEXT,
      vin TEXT UNIQUE,
      mileage INTEGER DEFAULT 0,
      created_at TEXT DEFAULT CURRENT_TIMESTAMP
    )
  `)
}

// IPC Handlers for car operations
function registerCarHandlers() {
  // Get all cars
  ipcMain.handle('car:getAll', () => {
    try {
      const cars = db.prepare('SELECT * FROM cars ORDER BY created_at DESC').all()
      return { success: true, data: cars }
    } catch (error) {
      console.error('Error fetching cars:', error)
      return { success: false, error: (error as Error).message }
    }
  })

  // Get car by ID
  ipcMain.handle('car:getById', (_event, id: number) => {
    try {
      const car = db.prepare('SELECT * FROM cars WHERE id = ?').get(id)
      return { success: true, data: car }
    } catch (error) {
      console.error('Error fetching car:', error)
      return { success: false, error: (error as Error).message }
    }
  })

  // Create car
  ipcMain.handle('car:create', (_event, carData: any) => {
    try {
      const stmt = db.prepare(
        'INSERT INTO cars (make, model, year, color, vin, mileage) VALUES (@make, @model, @year, @color, @vin, @mileage)',
      )
      const info = stmt.run(carData)
      const newCar = db.prepare('SELECT * FROM cars WHERE id = ?').get(info.lastInsertRowid)
      return { success: true, data: newCar }
    } catch (error) {
      console.error('Error creating car:', error)
      return { success: false, error: (error as Error).message }
    }
  })

  // Update car
  ipcMain.handle('car:update', (_event, id: number, carData: any) => {
    try {
      const stmt = db.prepare(
        'UPDATE cars SET make = @make, model = @model, year = @year, color = @color, vin = @vin, mileage = @mileage WHERE id = @id',
      )
      stmt.run({ ...carData, id })
      const updatedCar = db.prepare('SELECT * FROM cars WHERE id = ?').get(id)
      return { success: true, data: updatedCar }
    } catch (error) {
      console.error('Error updating car:', error)
      return { success: false, error: (error as Error).message }
    }
  })

  // Delete car
  ipcMain.handle('car:delete', (_event, id: number) => {
    try {
      const stmt = db.prepare('DELETE FROM cars WHERE id = ?')
      stmt.run(id)
      return { success: true }
    } catch (error) {
      console.error('Error deleting car:', error)
      return { success: false, error: (error as Error).message }
    }
  })
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

  // Seed cars table if empty
  const carCount = db.prepare('SELECT COUNT(*) as count FROM cars').get() as {
    count: number
  }

  if (carCount.count === 0) {
    const insertCar = db.prepare(
      'INSERT INTO cars (make, model, year, color, vin, mileage) VALUES (@make, @model, @year, @color, @vin, @mileage)',
    )

    const insertManyCars = db.transaction((cars) => {
      for (const car of cars) insertCar.run(car)
    })

    insertManyCars([
      {
        make: 'Toyota',
        model: 'Camry',
        year: 2022,
        color: 'Silver',
        vin: '1HGBH41JXMN109186',
        mileage: 15000,
      },
      {
        make: 'Honda',
        model: 'Civic',
        year: 2021,
        color: 'Blue',
        vin: '2HGFC2F59MH123456',
        mileage: 22000,
      },
      {
        make: 'Ford',
        model: 'F-150',
        year: 2023,
        color: 'Black',
        vin: '1FTFW1ET5MFC12345',
        mileage: 8500,
      },
      {
        make: 'Tesla',
        model: 'Model 3',
        year: 2023,
        color: 'White',
        vin: '5YJ3E1EA5MF123456',
        mileage: 5000,
      },
    ])

    console.log('Sample car data inserted')
  }

  const cars = db.prepare('SELECT * FROM cars').all()
  console.log('Current cars in database:', cars)

  // Register IPC handlers for car operations
  registerCarHandlers()

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
