import { Link } from 'react-router-dom'
import { useState } from 'react'
import { ArrowUpRight, MenuIcon, XIcon } from 'lucide-react'
import { Button, buttonVariants } from '@/components/ui/button'
import { KithMark } from '@/components/site/kith-mark'
import { cn } from '@/lib/utils'

const links = [
  { href: '#features', label: 'Features' },
  { href: '#workflow', label: 'How it works' },
  { href: '#faq', label: 'FAQ' },
]

export function SiteNav() {
  const [open, setOpen] = useState(false)

  return (
    <header className="fixed inset-x-0 top-4 z-50 flex justify-center px-4">
      <nav
        aria-label="Main"
        className="w-full max-w-3xl rounded-3xl border border-border/70 bg-card/80 p-3 shadow-[0_12px_40px_-24px_oklch(0.19_0.008_264/0.45)] backdrop-blur-2xl"
      >
        <div className="flex items-center justify-between gap-2 pl-2.5">
          <Link to="/" className="flex items-center gap-2 text-sm font-semibold tracking-tight">
            <KithMark className="size-5" />
            Kith
          </Link>

          <ul className="hidden items-center gap-1 md:flex">
            {links.map((link) => (
              <li key={link.href}>
                <a
                  href={link.href}
                  className="rounded-full px-3 py-1.5 text-sm text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
                >
                  {link.label}
                </a>
              </li>
            ))}
          </ul>

          <div className="flex items-center gap-1.5">
            <Link
              to="/login"
              className={cn(
                buttonVariants({ variant: 'ghost', size: 'sm' }),
                'hidden rounded-full sm:inline-flex',
              )}
            >
              Log in
            </Link>
            <Link
              to="/register"
              className={cn(buttonVariants({ size: 'sm' }), 'rounded-full pr-1.5 pl-3')}
            >
              Get started
              <span className="ml-1.5 flex size-5 items-center justify-center rounded-full bg-primary-foreground/15">
                <ArrowUpRight className="size-3" />
              </span>
            </Link>
            <Button
              variant="ghost"
              size="icon-sm"
              className="rounded-full md:hidden"
              aria-label={open ? 'Close menu' : 'Open menu'}
              aria-expanded={open}
              aria-controls="site-nav-mobile-menu"
              onClick={() => setOpen((value) => !value)}
            >
              {open ? <XIcon /> : <MenuIcon />}
            </Button>
          </div>
        </div>

        <div id="site-nav-mobile-menu" className={cn('md:hidden', open ? 'block' : 'hidden')}>
          <ul className="flex flex-col gap-1 px-1 pt-2 pb-1">
            {links.map((link) => (
              <li key={link.href}>
                <a
                  href={link.href}
                  onClick={() => setOpen(false)}
                  className="block rounded-2xl px-3 py-2 text-sm text-muted-foreground hover:bg-muted hover:text-foreground"
                >
                  {link.label}
                </a>
              </li>
            ))}
            <li>
              <Link
                to="/login"
                onClick={() => setOpen(false)}
                className="block rounded-2xl px-3 py-2 text-sm text-muted-foreground hover:bg-muted hover:text-foreground sm:hidden"
              >
                Log in
              </Link>
            </li>
          </ul>
        </div>
      </nav>
    </header>
  )
}
