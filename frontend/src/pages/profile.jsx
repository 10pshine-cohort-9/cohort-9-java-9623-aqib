import { useAuth } from '@/context/AuthContext'

export default function ProfilePage() {
  const { user } = useAuth()
  return (
    <div className="mx-auto max-w-3xl px-4 py-12">
      <h1 className="text-2xl font-semibold tracking-tight">My profile</h1>
      <p className="mt-2 text-sm text-muted-foreground">
        Profile for {user ? `${user.firstName} ${user.lastName}` : 'user'} — lands in the next exercise.
      </p>
    </div>
  )
}
