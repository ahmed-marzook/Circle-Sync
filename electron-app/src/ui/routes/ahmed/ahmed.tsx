import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/ahmed/ahmed')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/ahmed/"!</div>
}
