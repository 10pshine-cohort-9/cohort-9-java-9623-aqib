import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'

const faqs = [
  {
    question: 'Can I sign up with a phone number instead of an email?',
    answer:
      'Yes. Registration accepts either an email address or a phone number, and you log in with whichever one you used.',
  },
  {
    question: 'How many emails and phone numbers can one contact have?',
    answer:
      'As many as you need. Each entry is labeled — work, home, personal or other — so you always know which one to use.',
  },
  {
    question: 'What happens when I delete a contact?',
    answer:
      'You are asked to confirm first. After confirming, the contact leaves your list and the change is written to your activity trail.',
  },
  {
    question: 'Can I change my password later?',
    answer:
      'Open your profile screen and use the change password action. You can rotate it as often as you like.',
  },
  {
    question: 'Is my address book private?',
    answer:
      'Contacts are scoped to your account. Nothing is shared with other users unless you are on a Team plan and explicitly share a list.',
  },
]

export function Faq() {
  return (
    <section id="faq" className="border-t border-border bg-card/60">
      <div className="mx-auto grid max-w-6xl gap-10 px-6 py-24 lg:grid-cols-[0.8fr_1.2fr]">
        <div>
          <h2 className="text-4xl leading-tight font-bold tracking-tight text-balance">FAQ</h2>
          <p className="mt-4 text-base leading-relaxed text-muted-foreground text-pretty sm:text-lg">
            Questions, answered.
          </p>
        </div>

        <Accordion className="rounded-3xl border border-border/70 bg-background px-5 py-2">
          {faqs.map((faq) => (
            <AccordionItem key={faq.question} value={faq.question}>
              <AccordionTrigger className="py-4 text-base">{faq.question}</AccordionTrigger>
              <AccordionContent className="pb-4 text-sm leading-relaxed text-muted-foreground">
                {faq.answer}
              </AccordionContent>
            </AccordionItem>
          ))}
        </Accordion>
      </div>
    </section>
  )
}
