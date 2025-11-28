# Shared Code and Types

This folder contains code and types that are shared between different parts of the Electron application:

- **Main process** (`electron/main.ts`)
- **Renderer process** (`ui/*`)
- **Preload script** (`electron/preload.ts`)

## Structure

```
shared/
├── types/
│   ├── car.ts       # Car entity types
│   ├── ipc.ts       # IPC communication types
│   └── index.ts     # Type exports
└── index.ts         # Main exports
```

## Usage

### From Renderer Process (UI)

```typescript
import { Car, CarCreateInput, IpcResponse, ElectronAPI } from '@/shared'
```

### From Main Process or Preload Script

```typescript
import { Car, CarCreateInput, IpcResponse, ElectronAPI } from '../shared/index.js'
```

> **Note:** Electron code uses ES modules with explicit `.js` extensions due to `"module": "NodeNext"` in the tsconfig.

## Available Types

### Car Types (`types/car.ts`)

- `Car` - Complete car entity with all fields
- `CarCreateInput` - Input type for creating a new car
- `CarUpdateInput` - Input type for updating an existing car

### IPC Types (`types/ipc.ts`)

- `IpcResponse<T>` - Standard response wrapper for all IPC handlers
- `ElectronAPI` - Interface defining the secure API exposed to the renderer via contextBridge

## Adding New Shared Code

1. Create a new file in the appropriate subdirectory (e.g., `types/`, `constants/`, `utils/`)
2. Export your types/code from that file
3. Add the export to the appropriate `index.ts` file:
   - Add to `types/index.ts` if it's a type
   - The type will automatically be re-exported from `shared/index.ts`
4. Remember to use `.js` extensions in imports when importing TypeScript files within the shared folder

## Example: Adding a New Type

1. Create `types/user.ts`:

```typescript
export interface User {
  id: number
  name: string
  email: string
}
```

2. Update `types/index.ts`:

```typescript
export * from './car.js'
export * from './ipc.js'
export * from './user.js' // Add this line
```

3. Use it anywhere:

```typescript
import { User } from '@/shared'
```
