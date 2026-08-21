import { Link } from 'react-router-dom'
import { ArrowLeft } from 'lucide-react'
import { KithMark } from '@/components/site/kith-mark'

export function AuthShell({
  eyebrow,
  title,
  description,
  children,
  footer,
}) {
  return (
    <main className="grid min-h-svh lg:grid-cols-2">
      <div className="flex flex-col px-6 py-8 sm:px-12">
        <div className="flex items-center justify-between">
          <Link to="/" className="flex items-center gap-2 text-sm font-semibold tracking-tight">
            <KithMark className="size-5" />
            Kith
          </Link>
          <Link
            to="/"
            className="flex items-center gap-1.5 text-xs text-muted-foreground transition-colors hover:text-foreground"
          >
            <ArrowLeft className="size-3.5" />
            Back to site
          </Link>
        </div>

        <div className="flex flex-1 items-center justify-center py-12">
          <div className="w-full max-w-sm">
            <p className="text-xs font-medium tracking-widest text-muted-foreground uppercase">
              {eyebrow}
            </p>
            <h1 className="mt-3 text-3xl leading-tight font-semibold tracking-tight text-balance">
              {title}
            </h1>
            <p className="mt-2 text-sm leading-relaxed text-muted-foreground text-pretty">
              {description}
            </p>

            <div className="mt-8">{children}</div>

            <div className="mt-6 text-center text-sm text-muted-foreground">{footer}</div>
          </div>
        </div>
      </div>

      <div className="relative hidden overflow-hidden lg:block  p-3">
        <img
          src="/auth.webp"
          alt="Soft illustration of a green valley at dawn"
          className="h-full w-full object-cover rounded-2xl"
        />

      </div>
    </main>
  )
}
