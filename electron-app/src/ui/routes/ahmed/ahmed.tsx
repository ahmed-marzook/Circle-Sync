import { createFileRoute } from '@tanstack/react-router'
import { useEffect, useState } from 'react'
import { circleService } from '../../services/api/circleService'
import type { Circle } from '../../services/api/circleService'

export const Route = createFileRoute('/ahmed/ahmed')({
  component: RouteComponent,
})

function RouteComponent() {
  const [circles, setCircles] = useState<Circle[]>([])
  const [selected, setSelected] = useState<Circle | null>(null)

  useEffect(() => {
    ;(async () => {
      const list = await circleService.listCircles()
      setCircles(list)
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

  return (
    <div className="p-4">
      <div className="flex items-center gap-2 mb-3">
        <h2 className="text-lg font-semibold">Ahmed • CircleService Demo</h2>
        <button
          className="px-2 py-1 rounded bg-green-600 text-white"
          onClick={handleSeed}
        >
          Seed Circle
        </button>
        <button
          className="px-2 py-1 rounded bg-blue-600 text-white"
          onClick={handleCreate}
        >
          Create Circle
        </button>
        <button
          className="px-2 py-1 rounded bg-gray-200"
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
              No circles yet. Click <b>Seed Circle</b> or <b>Create Circle</b>.
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
  )
}
