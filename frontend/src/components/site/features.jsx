import { Search, Tag, ArrowUpDown, Activity, User } from 'lucide-react'

const features = [
  {
    Icon: Search,
    titleTop: 'Instant search.',
    titleBottom: '',
    description:
      'Find anything—name, last name, company or label as you type. No loading states, no waiting.',
    image: '/instant_search.png',
    span: 'lg:col-span-5 lg:row-span-2',
    layout: 'image-top',
  },
  {
    Icon: Tag,
    titleTop: 'Labeled details.',
    titleBottom: '',
    description:
      'Every email and phone number carries its own label — work, home, personal — so context travels with the contact.',
    image: '/detailed_label.png',
    span: 'lg:col-span-7 lg:row-span-2',
    layout: 'side',
  },
  {
    Icon: ArrowUpDown,
    titleTop: 'Import & export.',
    titleBottom: '',
    description:
      'Bring in a CSV from anywhere and take a clean copy of your address book with you whenever you want.',
    image: '/import_export.png',
    span: 'lg:col-span-6 lg:row-span-2',
    layout: 'side',
  },
  {
    Icon: Activity,
    titleTop: 'Activity trail.',
    titleBottom: '',
    description:
      'Every create, edit and delete is recorded, so you always know what changed and when it happened.',
    image: '/activity_trail.png',
    span: 'lg:col-span-6 lg:row-span-2',
    layout: 'side',
  },
  {
    Icon: User,
    titleTop: 'Your account, your rules.',
    titleBottom: '',
    description:
      'Sign up with an email or phone number. Your account stays private to you.',
    image: '/your_account.png',
    span: 'lg:col-span-12 lg:row-span-2',
    layout: 'side',
    imgClass: 'absolute right-0 inset-y-0 m-auto h-full w-full scale-[1.5] object-contain',
  },
]

function FeatureCard({ feature }) {
  const { Icon, titleTop, titleBottom, description, image, span, layout, imgClass } = feature

  return (
    <article className={`col-span-1 ${span}`}>
      <div className="relative isolate flex h-full min-h-[180px] flex-col overflow-hidden rounded-[24px] border border-border bg-card p-5 sm:min-h-[220px] sm:rounded-[32px] sm:p-8">
        {layout === 'image-top' && (
          <div className="relative z-10 flex h-full flex-col justify-end overflow-hidden">
            <img src={image} alt="" className="pointer-events-none absolute inset-x-0 top-0 z-0 mx-auto h-[75%] w-full object-contain" />
            <div className="relative z-10">
              <Heading titleTop={titleTop} titleBottom={titleBottom} />
              <Description>{description}</Description>
            </div>
          </div>
        )}

        {layout === 'side' && (
          <div className="relative z-10 grid h-full gap-5 sm:grid-cols-[0.7fr_1.3fr] sm:items-center">
            <div className="relative z-10">
              <Heading titleTop={titleTop} titleBottom={titleBottom} />
              <Description>{description}</Description>
            </div>
            <div className="relative h-full overflow-hidden">
              <img src={image} alt="" className={imgClass ?? 'absolute inset-0 m-auto h-full w-full scale-[1.2] object-contain'} />
            </div>
          </div>
        )}

        {layout === 'text-top' && (
          <div className="relative z-10 flex h-full min-h-0 flex-col">
            <div className="relative z-10">
              <Heading titleTop={titleTop} titleBottom={titleBottom} />
              <Description>{description}</Description>
            </div>
            <div className="relative flex-1 overflow-hidden">
              <img src={image} alt="" className="absolute inset-0 m-auto h-full w-full scale-[1.2] object-contain" />
            </div>
          </div>
        )}
      </div>
    </article>
  )
}

function Heading({ titleTop, titleBottom }) {
  return (
    <h3 className="font-[family-name:var(--font-instrument)] text-[22px] leading-[1.08] font-medium tracking-[-0.04em] text-foreground whitespace-nowrap sm:text-[28px] sm:leading-[1.05] sm:tracking-[-0.045em]">
      {titleBottom ? (
        <>
          <span className="text-muted-foreground">{titleTop} </span>
          <span className="text-foreground">{titleBottom}</span>
        </>
      ) : (
        titleTop
      )}
    </h3>
  )
}

function Description({ children }) {
  return (
    <p className="font-[family-name:var(--font-instrument)] mt-3 max-w-[360px] text-[13.5px] leading-[1.42] font-medium tracking-[-0.02em] text-muted-foreground sm:mt-4 sm:text-[15px] sm:leading-[1.45] sm:tracking-[-0.025em]">
      {children}
    </p>
  )
}

export function Features() {
  return (
    <section id="features" className="mx-auto max-w-6xl px-6 py-24">
      <div className="max-w-2xl">
        <h2 className="text-4xl leading-tight font-bold tracking-tight text-balance sm:text-5xl">
          Features
        </h2>
        <p className="mt-4 text-base leading-relaxed text-muted-foreground text-pretty sm:text-lg">
          A contact book that keeps up with you — built around the small moments,
          like finding a number before a meeting or fixing a typo in an address.
        </p>
      </div>

      <div className="relative z-10 mt-9 grid grid-cols-1 gap-4 sm:mt-12 sm:gap-5 lg:grid-cols-12 lg:grid-rows-[188px_188px_150px_150px_auto_auto]">
        {features.map((feature, i) => (
          <FeatureCard key={i} feature={feature} />
        ))}
      </div>
    </section>
  )
}
