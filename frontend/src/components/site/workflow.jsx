import { UserPlus, ListFilter, PencilLine } from 'lucide-react'

const steps = [
  {
    icon: UserPlus,
    title: 'Create your account',
    description: 'Register with an email address or a phone number. You land straight in your contact list.',
    image: '/create.png',
  },
  {
    icon: ListFilter,
    title: 'Bring your people in',
    description: 'Add contacts one by one or import a file. Group them as work, family, friends or clients.',
    image: '/add_contact.png',
  },
  {
    icon: PencilLine,
    title: 'Keep them current',
    description: 'Edit, favorite or delete in place with confirmation before anything leaves your address book.',
    image: '/favorite.png',
  },
]

export function Workflow() {
  return (
    <section id="workflow" className="border-y border-border bg-card/60">
      <div className="mx-auto max-w-6xl px-6 py-24 sm:py-32">
        <div className="max-w-xl">
          <h2 className="text-4xl leading-[1.1] font-semibold tracking-[-0.015em] text-balance sm:text-5xl">
            How it works.
          </h2>
          <p className="mt-4 max-w-[46ch] text-base leading-[1.5] text-muted-foreground text-pretty sm:text-lg">
            Three steps, then it just stays tidy — no setup wizard, no imports you
            have to babysit.
          </p>
        </div>

        <div className="mt-16 grid grid-cols-1 gap-10 sm:grid-cols-3">
          {steps.map((step, index) => (
            <div
              key={step.title}
              className="group flex h-full flex-col overflow-hidden rounded-[36px] border border-border bg-background transition-colors duration-300 hover:border-foreground/20"
            >
              <div className="flex h-69 items-end justify-center overflow-hidden border-t border-border bg-muted/30">
                <img
                  src={step.image}
                  alt={step.title}
                  className="h-full w-full object-cover object-bottom"
                />
              </div>

              <div className="p-7">
                <h3 className="mt-4 text-xl font-semibold tracking-tight text-foreground">
                  {step.title}
                </h3>
                <p className="mt-1.5 text-[0.9375rem] leading-[1.45] text-muted-foreground">
                  {step.description}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
