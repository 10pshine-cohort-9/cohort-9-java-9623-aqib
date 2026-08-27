import { cn } from '@/lib/utils'

export function KithMark({ className }) {
  return (
    <svg
      viewBox="0 0 24 24"
      fill="none"
      aria-hidden="true"
      className={cn('size-6', className)}
    >
      <circle cx="12" cy="12" r="10.5" className="fill-primary" />
      <circle cx="9" cy="10" r="2.6" className="fill-primary-foreground" />
      <path
        d="M4.6 19.2c1-2.7 2.6-4 4.4-4s3.4 1.3 4.4 4"
        className="stroke-primary-foreground"
        strokeWidth="1.6"
        strokeLinecap="round"
      />
      <path
        d="M16.4 8.4c1.6 0 2.8 1.2 2.8 2.8 0 2-2.8 4-2.8 4"
        className="stroke-accent"
        strokeWidth="1.6"
        strokeLinecap="round"
      />
    </svg>
  )
}
