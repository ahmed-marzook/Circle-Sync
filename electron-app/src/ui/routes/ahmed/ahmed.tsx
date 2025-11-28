import { createFileRoute } from '@tanstack/react-router'
import { useEffect, useState } from 'react'
import { circleService } from '../../services/api/circleService'
import type { Circle } from '../../services/api/circleService'
import { carService } from '../../services/api/carService'
import type { Car } from '@shared/index'

export const Route = createFileRoute('/ahmed/ahmed')({
  component: RouteComponent,
})

function RouteComponent() {
  const [circles, setCircles] = useState<Circle[]>([])
  const [selected, setSelected] = useState<Circle | null>(null)
  const [cars, setCars] = useState<Car[]>([])
  const [selectedCar, setSelectedCar] = useState<Car | null>(null)

  useEffect(() => {
    ;(async () => {
      const list = await circleService.listCircles()
      setCircles(list)

      // Fetch cars from database
      const carList = await carService.getAllCars()
      setCars(carList)
    })()
  }, [])

  async function handleSeed() {
    const c = circleService.seedOne()
    console.debug('[Ahmed] seeded', c)
    const list = await circleService.listCircles()
    setCircles(list)
    setSelected(c)
  }

  async function handleCreate() {
    const c = await circleService.createCircle({ name: 'Ahmed View Circle' })
    console.debug('[Ahmed] created', c)
    const list = await circleService.listCircles()
    setCircles(list)
    setSelected(c)
  }

  async function handleCreateCar() {
    const carData = carService.seedOne()
    const newCar = await carService.createCar(carData)
    console.debug('[Ahmed] created car', newCar)
    const carList = await carService.getAllCars()
    setCars(carList)
    if (newCar) setSelectedCar(newCar)
  }

  async function handleDeleteCar(id: number) {
    const success = await carService.deleteCar(id)
    if (success) {
      console.debug('[Ahmed] deleted car', id)
      const carList = await carService.getAllCars()
      setCars(carList)
      if (selectedCar?.id === id) setSelectedCar(null)
    }
  }

  return (
    <div className="p-4 space-y-6">
      {/* Car Service Demo Section */}
      <div>
        <div className="flex items-center gap-2 mb-3">
          <h2 className="text-lg font-semibold">Car Database (SQLite)</h2>
          <button
            className="px-2 py-1 rounded bg-green-600 text-white text-sm"
            onClick={handleCreateCar}
          >
            Add Random Car
          </button>
          <button
            className="px-2 py-1 rounded bg-gray-200 text-sm"
            onClick={async () => setCars(await carService.getAllCars())}
          >
            Refresh
          </button>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <div className="text-sm text-muted-foreground mb-2">
              Cars in Database ({cars.length})
            </div>
            {cars.length === 0 ? (
              <div className="text-sm text-gray-500">
                No cars yet. Sample cars should be loaded automatically.
              </div>
            ) : (
              <div className="space-y-2 max-h-96 overflow-y-auto">
                {cars.map((car) => (
                  <div
                    key={car.id}
                    className="border rounded p-3 hover:shadow cursor-pointer"
                    onClick={() => setSelectedCar(car)}
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <div className="font-semibold">
                          {car.make} {car.model}
                        </div>
                        <div className="text-xs text-gray-500">
                          {car.year} • {car.color || 'No color'}
                        </div>
                        <div className="text-xs text-gray-400">
                          {car.mileage?.toLocaleString() || 0} miles
                        </div>
                      </div>
                      <button
                        className="px-2 py-1 rounded bg-red-500 text-white text-xs"
                        onClick={(e) => {
                          e.stopPropagation()
                          handleDeleteCar(car.id)
                        }}
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div>
            <div className="text-sm text-muted-foreground mb-2">
              Selected Car Details
            </div>
            {selectedCar ? (
              <div className="border rounded p-4">
                <div className="space-y-2">
                  <div>
                    <span className="font-semibold text-lg">
                      {selectedCar.make} {selectedCar.model}
                    </span>
                  </div>
                  <div className="text-sm">
                    <span className="font-medium">Year:</span>{' '}
                    {selectedCar.year}
                  </div>
                  <div className="text-sm">
                    <span className="font-medium">Color:</span>{' '}
                    {selectedCar.color || 'N/A'}
                  </div>
                  <div className="text-sm">
                    <span className="font-medium">VIN:</span>{' '}
                    {selectedCar.vin || 'N/A'}
                  </div>
                  <div className="text-sm">
                    <span className="font-medium">Mileage:</span>{' '}
                    {selectedCar.mileage?.toLocaleString() || 0} miles
                  </div>
                  <div className="text-xs text-gray-400 mt-2">
                    ID: {selectedCar.id}
                  </div>
                </div>
              </div>
            ) : (
              <div className="text-sm text-gray-500">
                Click a car to see details
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Circle Service Demo Section */}
      <div>
        <div className="flex items-center gap-2 mb-3">
          <h2 className="text-lg font-semibold">
            CircleService Demo (In-Memory)
          </h2>
          <button
            className="px-2 py-1 rounded bg-green-600 text-white text-sm"
            onClick={handleSeed}
          >
            Seed Circle
          </button>
          <button
            className="px-2 py-1 rounded bg-blue-600 text-white text-sm"
            onClick={handleCreate}
          >
            Create Circle
          </button>
          <button
            className="px-2 py-1 rounded bg-gray-200 text-sm"
            onClick={async () => setCircles(await circleService.listCircles())}
          >
            Refresh
          </button>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <div className="text-sm text-muted-foreground mb-2">
              Circles (in-memory)
            </div>
            {circles.length === 0 ? (
              <div className="text-sm text-gray-500">
                No circles yet. Click <b>Seed Circle</b> or <b>Create Circle</b>
                .
              </div>
            ) : (
              <ul className="space-y-2">
                {circles.map((c) => (
                  <li
                    key={c.id}
                    className="border rounded p-2 hover:shadow cursor-pointer"
                    onClick={() => setSelected(c)}
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <div className="font-semibold">{c.name}</div>
                        <div className="text-xs text-gray-500">
                          {c.privacy} • {c.circleType}
                        </div>
                      </div>
                      <div className="text-xs text-gray-400">
                        {c.members?.length ?? 0} members
                      </div>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>

          <div>
            <div className="text-sm text-muted-foreground mb-2">
              Selected circle
            </div>
            {selected ? (
              <div className="border rounded p-2">
                <div className="font-semibold">{selected.name}</div>
                <div className="text-xs text-gray-500 mb-2">
                  {selected.privacy} • {selected.circleType}
                </div>
                <div className="text-sm mb-2">{selected.description}</div>
                <div className="text-xs font-medium">Members</div>
                <ul className="mt-2">
                  {selected.members?.map((m) => (
                    <li key={m.userId} className="text-sm">
                      {m.nickname || m.userName}{' '}
                      <span className="text-xs text-gray-400">({m.role})</span>
                    </li>
                  ))}
                </ul>
              </div>
            ) : (
              <div className="text-sm text-gray-500">
                Click a circle to see details
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
