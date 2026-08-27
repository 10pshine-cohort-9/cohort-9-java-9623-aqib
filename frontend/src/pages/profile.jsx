import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ArrowLeft, KeyRound, LogOut, ShieldCheck, UserRound } from 'lucide-react'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Spinner } from '@/components/ui/spinner'
import { ChangePasswordDialog } from '@/components/app/change-password-dialog'
import { useAuth } from '@/context/AuthContext'
import { initials } from '@/lib/contacts'
import { toast } from 'sonner'

export default function ProfilePage() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [showPasswordModal, setShowPasswordModal] = useState(false)
  const [pendingLogout, setPendingLogout] = useState(false)

  useEffect(() => {
    document.title = 'Profile · Kith'
  }, [])

  async function handleLogout() {
    setPendingLogout(true)
    try {
      await logout()
      navigate('/login', { replace: true })
    } catch {
      toast.error('Failed to log out. Please try again.')
    } finally {
      setPendingLogout(false)
    }
  }

  if (!user) {
    return (
      <div className="flex items-center justify-center py-20 text-muted-foreground">
        <Spinner className="size-5" />
        <span className="ml-2 text-sm">Loading profile…</span>
      </div>
    )
  }

  const details = [
    { label: 'First name', value: user.firstName },
    { label: 'Last name', value: user.lastName },
    { label: 'Email', value: user.email || '—' },
    { label: 'Phone', value: user.phone || '—' },
  ]

  return (
    <div className="mx-auto flex max-w-3xl flex-col gap-8 px-4 py-8 sm:px-8 lg:py-12">
      <Button
        variant="ghost"
        size="sm"
        className="-ml-2 w-fit text-muted-foreground"
        nativeButton={false}
        render={
          <Link to="/contacts">
            <ArrowLeft data-icon="inline-start" />
            Back to contacts
          </Link>
        }
      />

      <div className="flex flex-col gap-5 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex items-center gap-4">
          <Avatar className="size-16 rounded-xl">
            <AvatarFallback className="rounded-xl bg-primary text-base text-primary-foreground">
              {initials(user)}
            </AvatarFallback>
          </Avatar>
          <div className="flex flex-col gap-1">
            <h1 className="font-serif text-2xl tracking-tight sm:text-3xl">
              {user.firstName} {user.lastName}
            </h1>
            <p className="text-sm text-muted-foreground">{user.email || user.phone || ''}</p>
          </div>
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <UserRound className="size-4 text-muted-foreground" />
            Account details
          </CardTitle>
          <CardDescription>Your registration information.</CardDescription>
        </CardHeader>
        <CardContent>
          <dl className="grid gap-4 sm:grid-cols-2">
            {details.map((detail) => (
              <div key={detail.label} className="flex flex-col gap-0.5">
                <dt className="text-[10px] font-medium tracking-widest text-muted-foreground uppercase">
                  {detail.label}
                </dt>
                <dd className="text-sm">{detail.value}</dd>
              </div>
            ))}
          </dl>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <ShieldCheck className="size-4 text-muted-foreground" />
            Security
          </CardTitle>
          <CardDescription>Manage your password and session.</CardDescription>
        </CardHeader>
        <CardContent className="flex flex-wrap gap-3">
          <Button className="rounded-full" onClick={() => setShowPasswordModal(true)}>
            <KeyRound data-icon="inline-start" />
            Change password
          </Button>
          <Button
            variant="ghost"
            className="rounded-full text-destructive hover:bg-destructive/10"
            disabled={pendingLogout}
            onClick={handleLogout}
          >
            {pendingLogout ? <Spinner data-icon="inline-start" /> : <LogOut data-icon="inline-start" />}
            {pendingLogout ? 'Logging out…' : 'Log out'}
          </Button>
        </CardContent>
      </Card>

      <ChangePasswordDialog
        open={showPasswordModal}
        onOpenChange={setShowPasswordModal}
        onChanged={() => toast.success('Password changed', { description: 'Use it next time you log in.' })}
      />
    </div>
  )
}
