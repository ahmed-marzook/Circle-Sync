# Electron + React + Vite + TanStack Router Setup Guide

This guide shows how to build a desktop app using Electron, React, Vite, and TanStack Router with file-based routing and a TypeScript Electron main process.

## 1. Create the React project using Vite

In an empty folder, run:

npm create vite .

When prompted, choose:

Framework: React

Variant: TypeScript

Routing: TanStack Router – File-Based Routing

## 2. Move UI files into src/ui

Restructure your src folder so React lives under src/ui:

src/
ui/
main.tsx
routes/
components/
(other React files)

Move the existing React/Vite files into src/ui.

## 3. Update vite.config.ts

Point TanStack Router and the build output to the new locations:

import { defineConfig } from 'vite'
import { devtools } from '@tanstack/devtools-vite'
import viteReact from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { tanstackRouter } from '@tanstack/router-plugin/vite'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
plugins: [
devtools(),
tanstackRouter({
target: 'react',
autoCodeSplitting: true,
routesDirectory: 'src/ui/routes',
generatedRouteTree: 'src/ui/routeTree.gen.ts',
}),
viteReact(),
tailwindcss(),
],
resolve: {
alias: {
'@': fileURLToPath(new URL('./src', import.meta.url)),
},
},
base: './',
build: {
outDir: 'dist-react',
},
})

## 4. Update index.html entry

In index.html, point the script to the new React entry:

<script type="module" src="/src/ui/main.tsx"></script>

## 5. Install Electron

Install Electron as a dev dependency:

npm install --save-dev electron

## 6. Add Electron main process in TypeScript

Create a folder:

src/
electron/
main.ts
tsconfig.json

## 6.1 src/electron/main.ts

```typescript
import { app, BrowserWindow } from "electron";
import path from "node:path";

function createWindow() {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
  });

  // Load the built React app
  win.loadFile(path.join(app.getAppPath(), "/dist-react/index.html"));
}

app.whenReady().then(() => {
  createWindow();

  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") {
    app.quit();
  }
});
```

## 6.2 src/electron/tsconfig.json

This TypeScript config compiles Electron’s TS files into dist-electron at the project root and reuses the root tsconfig settings:

```json
{
  "extends": "../../tsconfig.json",
  "compilerOptions": {
    "outDir": "../../dist-electron",
    "rootDir": "./",
    "module": "NodeNext",
    "moduleResolution": "NodeNext",
    "target": "ES2022",
    "lib": ["ES2022", "DOM"],
    "types": ["node"],
    "noEmit": false
  },
  "include": ["./**/*.ts"],
  "exclude": []
}
```

## 7. Update package.json

Here’s an updated package.json that:

Sets "main" to the compiled Electron entry (dist-electron/main.js)

Adds scripts for:

React dev

Electron dev

Electron TypeScript transpile

Build React + Electron

```json
{
  "name": ".",
  "private": true,
  "type": "module",
  "main": "dist-electron/main.js",
  "scripts": {
    "dev:react": "vite --port 3000",
    "dev:electron": "npm run transpile:electron && electron .",
    "transpile:electron": "tsc --project src/electron/tsconfig.json",
    "build": "vite build && npm run transpile:electron",
    "serve": "vite preview",
    "test": "vitest run",
    "lint": "eslint",
    "format": "prettier",
    "check": "prettier --write . && eslint --fix",
    "storybook": "storybook dev -p 6006",
    "build-storybook": "storybook build"
  },
  "dependencies": {
    "@storybook/react-vite": "^9.1.9",
    "@tailwindcss/vite": "^4.0.6",
    "@tanstack/react-devtools": "^0.7.0",
    "@tanstack/react-query": "^5.66.5",
    "@tanstack/react-query-devtools": "^5.84.2",
    "@tanstack/react-router": "^1.132.0",
    "@tanstack/react-router-devtools": "^1.132.0",
    "@tanstack/router-plugin": "^1.132.0",
    "class-variance-authority": "^0.7.1",
    "clsx": "^2.1.1",
    "lucide-react": "^0.544.0",
    "react": "^19.2.0",
    "react-dom": "^19.2.0",
    "storybook": "^9.1.9",
    "tailwind-merge": "^3.0.2",
    "tailwindcss": "^4.0.6",
    "tw-animate-css": "^1.3.6"
  },
  "devDependencies": {
    "@tanstack/devtools-vite": "^0.3.11",
    "@tanstack/eslint-config": "^0.3.0",
    "@testing-library/dom": "^10.4.0",
    "@testing-library/react": "^16.2.0",
    "@types/node": "^22.10.2",
    "@types/react": "^19.2.0",
    "@types/react-dom": "^19.2.0",
    "@vitejs/plugin-react": "^5.0.4",
    "electron": "^39.1.2",
    "jsdom": "^27.0.0",
    "prettier": "^3.5.3",
    "typescript": "^5.7.2",
    "vite": "^7.1.7",
    "vitest": "^3.0.5",
    "web-vitals": "^5.1.0"
  }
}
```

## 8. Run the app

Dev (React only)

    npm run dev:react

Build React + Electron

    npm run build

Run Electron with compiled main

    npm run dev:electron
