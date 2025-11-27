// Import `api` to call the backend; if the backend is down we'll fall back to in-memory store
import { api } from './api'

// Simple types for Circle and Member
export interface Member {
  userId: string
  userName: string
  userAvatar?: string
  nickname?: string
  role?: 'ADMIN' | 'MEMBER' | 'VIEWER' | string
}

export interface Circle {
  id: string
  name: string
  description?: string
  circleType?: string
  privacy?: 'PUBLIC' | 'PRIVATE' | 'INVITE_ONLY' | string
  avatarUrl?: string
  settings?: Record<string, any>
  inviteCode?: string
  members?: Member[]
  createdAt?: string
}

// Helper for generating a UUID-like value
function simpleUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0
    const v = c === 'x' ? r : (r & 0x3) | 0x8
    return v.toString(16)
  })
}

function randName(prefix = 'Circle') {
  return `${prefix}-${Math.random().toString(36).slice(2, 8)}`
}

function randUser(): Member {
  return {
    userId: simpleUUID(),
    userName: `user_${Math.random().toString(36).slice(2, 10)}`,
    userAvatar: `https://picsum.photos/seed/${Math.random().toString(36).slice(2, 8)}/100/100`,
    nickname: `nick_${Math.random().toString(36).slice(2, 6)}`,
    role: ['ADMIN', 'MEMBER', 'VIEWER'][Math.floor(Math.random() * 3)],
  }
}

// A small in-memory store for dummy data. This makes the service usable without API.
const inMemoryStore: Record<string, Circle> = {}

export class CircleService {
  // Create a circle. For now we'll create it locally and return a dummy object.
  // In future, this method can call `api.post('/circles', body)`.
  async createCircle(payload: Partial<Circle>): Promise<Circle> {
    try {
      // Call the remote API (POST /circles)
      const res = await api.post('/circles', payload)
      // Some APIs return 201 for created resources
      if (res?.data) {
        // Optionally keep in-memory cache in sync
        try {
          inMemoryStore[res.data.id] = res.data
        } catch (err) {
          // ignore cache errors
        }
        return res.data
      }
    } catch (err) {
      console.warn(
        '[CircleService] createCircle remote call failed, falling back to in-memory:',
        err,
      )
    }

    // Fallback to in-memory store if API isn't available or returns nothing
    const id = simpleUUID()
    const inviteCode = Math.random().toString(36).slice(2, 10).toUpperCase()
    const circle: Circle = {
      id,
      name: payload.name || randName('Circle'),
      description:
        payload.description ||
        'A fallback (in-memory) circle created from the UI',
      circleType: payload.circleType || 'OTHER',
      privacy: payload.privacy || 'PUBLIC',
      avatarUrl: payload.avatarUrl ?? '',
      settings: payload.settings ?? { color: 'blue' },
      inviteCode,
      members: payload.members ?? [randUser()],
      createdAt: new Date().toISOString(),
    }

    inMemoryStore[id] = circle
    return circle
  }

  // Fetch a circle by id. Check in-memory store first. If not found, fall back to an API call.
  async getCircle(circleId: string): Promise<Circle | null> {
    try {
      const res = await api.get(`/circles/${encodeURIComponent(circleId)}`)
      if (res?.data) {
        inMemoryStore[res.data.id] = res.data
        return res.data
      }
    } catch (err) {
      console.warn(
        '[CircleService] getCircle remote call failed, falling back to in-memory:',
        err,
      )
    }

    // Fallback to memory if the remote call failed or returned nothing
    if (inMemoryStore[circleId]) {
      return inMemoryStore[circleId]
    }
    return null
  }

  // Simple list method that returns all created circles (in-memory) or an empty array
  async listCircles(): Promise<Circle[]> {
    try {
      const res = await api.get('/circles')
      if (Array.isArray(res?.data)) {
        // Keep cache in memory in case of offline fallback
        for (const c of res.data) {
          inMemoryStore[c.id] = c
        }
        return res.data
      }
    } catch (err) {
      console.warn(
        '[CircleService] listCircles remote call failed, falling back to in-memory:',
        err,
      )
    }

    const list = Object.values(inMemoryStore)
    return list
  }

  // Utility to seed a dummy circle (handy for UI development)
  seedOne(dummy?: Partial<Circle>): Circle {
    // Try to persist to the remote API, if it's available. If not, fall back to in-memory seeding.
    const dummyPayload: Partial<Circle> = {
      name: dummy?.name || randName('Circle'),
      description: dummy?.description || 'Seeded dummy circle',
      circleType: dummy?.circleType || 'HOBBY',
      privacy: dummy?.privacy || 'PRIVATE',
      avatarUrl: dummy?.avatarUrl || '',
      settings: dummy?.settings || { color: 'purple' },
      members: dummy?.members || [randUser(), randUser()],
    }

    // attempt to create on backend (fire-and-forget async)
    ;(async () => {
      try {
        const created = await this.createCircle(dummyPayload)
        // ensure cached in-memory entry is the created one
        inMemoryStore[created.id] = created
      } catch (err) {
        // if remote fails, we'll fall back to the local approach below
        console.warn(
          '[CircleService] seedOne createCircle via API failed, seeding in-memory:',
          err,
        )
      }
    })()

    const circle = {
      id: simpleUUID(),
      name: dummy?.name || randName('Circle'),
      description: dummy?.description || 'Seeded dummy circle',
      circleType: dummy?.circleType || 'HOBBY',
      privacy: dummy?.privacy || 'PRIVATE',
      avatarUrl: dummy?.avatarUrl || '',
      settings: dummy?.settings || { color: 'purple' },
      inviteCode:
        dummy?.inviteCode ||
        Math.random().toString(36).slice(2, 8).toUpperCase(),
      members: dummy?.members || [randUser(), randUser()],
      createdAt: new Date().toISOString(),
    } as Circle
    inMemoryStore[circle.id] = circle
    return circle
  }
}

export const circleService = new CircleService()

// Quick usage example:
// import { circleService } from './api/circleService';
// const c = await circleService.createCircle({ name: 'My Circle' });
// const got = await circleService.getCircle(c.id);
// const list = await circleService.listCircles();
