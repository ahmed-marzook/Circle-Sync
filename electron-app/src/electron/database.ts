import Database from 'better-sqlite3'
import { getDatabasePath } from './pathResolver.js'

let db: Database.Database

/**
 * Initialize database schema
 * Creates all necessary tables if they don't exist
 */
function initSchema() {
  // Create cats table (example/legacy)
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

  console.log('Database schema initialized')
}

/**
 * Seed the database with sample data
 * Only runs if tables are empty
 */
function seedDatabase() {
  // Seed cats table if empty
  const catCount = db.prepare('SELECT COUNT(*) as count FROM cats').get() as {
    count: number
  }

  if (catCount.count === 0) {
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

    console.log('Sample cat data inserted')
  }

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

  // Log current data
  const cats = db.prepare('SELECT * FROM cats').all()
  console.log('Current cats in database:', cats)

  const cars = db.prepare('SELECT * FROM cars').all()
  console.log('Current cars in database:', cars)
}

/**
 * Initialize the database
 * Must be called after app.whenReady()
 */
export function initDatabase() {
  const dbPath = getDatabasePath()
  console.log('Database location:', dbPath)

  db = new Database(dbPath, { verbose: console.log })

  // Initialize schema first
  initSchema()

  // Seed with sample data
  seedDatabase()

  return db
}

/**
 * Get the database instance
 */
export function getDatabase(): Database.Database {
  if (!db) {
    throw new Error('Database not initialized. Call initDatabase() first.')
  }
  return db
}

/**
 * Close the database connection
 */
export function closeDatabase() {
  try {
    if (db) {
      db.close()
      console.log('Database connection closed')
    }
  } catch (error) {
    console.error('Error closing database:', error)
  }
}
