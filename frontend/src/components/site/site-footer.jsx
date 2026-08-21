import { Link } from 'react-router-dom'
import { ArrowUpRight } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
import { KithMark } from '@/components/site/kith-mark'

const columns = [
  {
    title: 'Product',
    links: [
      { label: 'Features', href: '#features' },
      { label: 'How it works', href: '#workflow' },
      { label: 'FAQ', href: '#faq' },
    ],
  },
  {
    title: 'App',
    links: [
      { label: 'Log in', href: '/login' },
      { label: 'Create account', href: '/register' },
      { label: 'Contacts', href: '/contacts' },
      { label: 'Profile', href: '/profile' },
    ],
  },
  {
    title: 'Company',
    links: [
      { label: 'About', href: '#' },
      { label: 'Changelog', href: '#' },
      { label: 'Privacy', href: '#' },
      { label: 'Contact', href: '#' },
    ],
  },
]

export function SiteFooter() {
  return (
    <footer className="mx-auto max-w-6xl px-6 pt-24 pb-10">
      <div className="flex flex-col items-center gap-6 rounded-[2rem] bg-primary px-8 py-16 text-center text-primary-foreground">
        <h2 className="max-w-2xl text-4xl leading-tight font-semibold tracking-tight text-balance sm:text-5xl">
          Start keeping better track of your people
        </h2>
        <p className="max-w-md text-sm leading-relaxed text-primary-foreground/70">
          Free for your first 500 contacts. No credit card, no import gymnastics.
        </p>
        <Button
          size="lg"
          variant="secondary"
          className="h-11 rounded-full pr-1.5 pl-5"
          render={
            <Link to="/register">
              Create your account
              <span className="ml-2 flex size-8 items-center justify-center rounded-full bg-primary/10">
                <ArrowUpRight className="size-4" />
              </span>
            </Link>
          }
        />
      </div>

      <div className="mt-14 grid gap-10 sm:grid-cols-2 lg:grid-cols-4">
        <div className="flex flex-col gap-3">
          <Link to="/" className="flex items-center gap-2 text-sm font-semibold tracking-tight">
            <KithMark className="size-5" />
            Kith
          </Link>
          <p className="max-w-56 text-sm leading-relaxed text-muted-foreground">
            A calm contact manager for people who like to stay in touch.
          </p>
        </div>

        {columns.map((column) => (
          <nav key={column.title} aria-label={column.title} className="flex flex-col gap-3">
            <p className="text-xs font-medium tracking-widest text-muted-foreground uppercase">
              {column.title}
            </p>
            <ul className="flex flex-col gap-2">
              {column.links.map((link) => (
                <li key={link.label}>
                  {link.href.startsWith('#') ? (
                    <a
                      href={link.href}
                      className="text-sm text-muted-foreground transition-colors hover:text-foreground"
                    >
                      {link.label}
                    </a>
                  ) : (
                    <Link
                      to={link.href}
                      className="text-sm text-muted-foreground transition-colors hover:text-foreground"
                    >
                      {link.label}
                    </Link>
                  )}
                </li>
              ))}
            </ul>
          </nav>
        ))}
      </div>

      <Separator className="mt-12" />

      <div className="flex flex-col items-center justify-between gap-2 pt-6 text-xs text-muted-foreground sm:flex-row">
        <p>© {new Date().getFullYear()} Kith. A frontend demo.</p>
        <p>Designed for calm address books.</p>
      </div>
    </footer>
  )
}
