import { Link } from 'react-router-dom'
import { ArrowUpRight, PlayCircle } from 'lucide-react'
import { buttonVariants } from '@/components/ui/button'
import { cn } from '@/lib/utils'
import { AppPreview } from '@/components/site/app-preview'

export function Hero() {
  return (
    <section className="relative overflow-hidden pt-32 pb-0">
      {/* Mountain backdrop — frames the headline in the empty valley between the peaks */}

        <img
          src="/background.png"
          alt=""
          className="hero-fade absolute inset-0 w-full h-full object-cover"
        />

      <div className="relative z-10 mx-auto flex max-w-5xl flex-col items-center px-6 text-center">

        <h1 className="mt-6 max-w-3xl text-5xl leading-[0.95] font-semibold tracking-tight text-balance sm:text-6xl lg:text-7xl">
          Everyone you know in one calm place
        </h1>

        <p className="mt-5 max-w-xl text-base leading-relaxed text-muted-foreground text-pretty">
          Kith keeps every name, number and address book detail tidy — searchable in a keystroke,
          shareable in a click, and never duplicated again.
        </p>

        <div className="mt-8 flex flex-col items-center gap-3 sm:flex-row">
          <Link
            to="/register"
            className={cn(buttonVariants({ size: 'lg' }), 'h-11 rounded-full pr-1.5 pl-5 text-sm')}
          >
            Get started free
            <span className="ml-2 flex size-8 items-center justify-center rounded-full bg-primary-foreground/15">
              <ArrowUpRight className="size-4" />
            </span>
          </Link>
          <a
            href="#workflow"
            className={cn(
              buttonVariants({ variant: 'ghost', size: 'lg' }),
              'h-11 rounded-full px-4 text-sm text-muted-foreground',
            )}
          >
            <PlayCircle data-icon="inline-start" />
            Watch the tour
          </a>
        </div>
      </div>

      <div className="relative z-10 mx-auto mt-24 max-w-7xl px-6 pb-8">
        <AppPreview />
      </div>
    </section>
  )
}
