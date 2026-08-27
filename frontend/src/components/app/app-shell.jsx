import { Link, useLocation, useNavigate } from 'react-router-dom'
import { LogOut, UserCog, Users } from 'lucide-react'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
import { KithMark } from '@/components/site/kith-mark'
import { useAuth } from '@/context/AuthContext'
import { initials } from '@/lib/contacts'
import { cn } from '@/lib/utils'

export function AppShell({ children }) {
  const { user, logout } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()

  const onContacts = location.pathname.startsWith('/contacts')
  const onProfile = location.pathname.startsWith('/profile')

  async function handleLogout() {
    await logout()
    navigate('/login', { replace: true })
  }

  const navItems = [
    { key: 'contacts', label: 'Contacts', icon: Users, active: onContacts, to: '/contacts' },
    { key: 'profile', label: 'Profile', icon: UserCog, active: onProfile, to: '/profile' },
  ]

  return (
    <div className="flex min-h-svh flex-col lg:flex-row">
      <aside className="flex shrink-0 flex-col gap-4 border-b border-border bg-sidebar p-4 lg:w-64 lg:border-r lg:border-b-0">
        <Link to="/" className="flex items-center gap-2 px-1 text-sm font-semibold tracking-tight">
          <KithMark className="size-5" />
          Kith
        </Link>

        <nav aria-label="Main" className="flex flex-col gap-1">
          <p className="px-2 pb-1 text-[10px] font-medium tracking-widest text-muted-foreground uppercase">
            Menu
          </p>
          <ul className="flex gap-1 overflow-x-auto pb-1 lg:flex-col lg:overflow-visible lg:pb-0">
            {navItems.map((item) => (
              <li key={item.key} className="shrink-0 lg:shrink">
                <Link
                  to={item.to}
                  aria-current={item.active ? 'page' : undefined}
                  className={cn(
                    'flex items-center gap-2 rounded-xl px-2.5 py-2 text-sm transition-colors',
                    item.active
                      ? 'bg-primary text-primary-foreground'
                      : 'text-muted-foreground hover:bg-muted hover:text-foreground',
                  )}
                >
                  <item.icon className="size-4" />
                  <span className="flex-1 whitespace-nowrap">{item.label}</span>
                </Link>
              </li>
            ))}
          </ul>
        </nav>

        <Separator className="hidden lg:block" />

        <div className="hidden flex-1 lg:block" />

        <div className="flex items-center justify-between gap-2 rounded-xl border border-border/70 bg-card p-2.5">
          <Link to="/profile" className="flex min-w-0 items-center gap-2.5">
            <Avatar className="size-8">
              <AvatarFallback className="text-[10px]">
                {user ? initials(user) : '?'}
              </AvatarFallback>
            </Avatar>
            <div className="min-w-0">
              <p className="truncate text-xs font-medium">
                {user ? `${user.firstName} ${user.lastName}` : 'Account'}
              </p>
              <p className="truncate text-[11px] text-muted-foreground">
                {user?.email || user?.phone || ''}
              </p>
            </div>
          </Link>
          <div className="flex items-center gap-0.5">
            <Button
              variant="ghost"
              size="icon-sm"
              aria-label="Open profile"
              nativeButton={false}
              render={
                <Link to="/profile">
                  <UserCog />
                </Link>
              }
            />
            <Button variant="ghost" size="icon-sm" aria-label="Log out" onClick={handleLogout}>
              <LogOut />
            </Button>
          </div>
        </div>
      </aside>

      <div className="min-w-0 flex-1">{children}</div>
    </div>
  )
}
