import { useEffect, useState } from 'react'
import { Plus, Trash2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Field, FieldError, FieldGroup, FieldLabel, FieldSeparator } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Spinner } from '@/components/ui/spinner'
import { createId, emailLabels, emptyContact, phoneLabels } from '@/lib/contacts'
import { getErrorMessage } from '@/services/api'

export function ContactFormDialog({ open, onOpenChange, contact, onSubmit }) {
  const mode = contact ? 'edit' : 'create'
  const [draft, setDraft] = useState(emptyContact)
  const [errors, setErrors] = useState({})
  const [submitError, setSubmitError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    if (!open) return
    setErrors({})
    setSubmitError('')
    setDraft(
      contact
        ? {
            firstName: contact.firstName ?? '',
            lastName: contact.lastName ?? '',
            title: contact.title ?? '',
            emails: (contact.emails ?? []).map((email) => ({ ...email })),
            phones: (contact.phones ?? []).map((phone) => ({ ...phone })),
          }
        : {
            ...emptyContact,
            emails: [{ id: createId(), label: 'WORK', value: '' }],
            phones: [{ id: createId(), label: 'WORK', value: '' }],
          },
    )
  }, [open, contact])

  function set(key, value) {
    setDraft((current) => ({ ...current, [key]: value }))
  }

  function validate() {
    const next = {}
    if (!draft.firstName.trim()) next.firstName = 'First name is required.'
    if (!draft.lastName.trim()) next.lastName = 'Last name is required.'

    const filledEmails = draft.emails.filter((email) => email.value.trim())
    const filledPhones = draft.phones.filter((phone) => phone.value.trim())
    if (filledEmails.length === 0 && filledPhones.length === 0) {
      next.emails = 'Add at least one email address or phone number.'
    } else {
      if (filledEmails.some((email) => !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email.value.trim()))) {
        next.emails = 'One of the email addresses is not valid.'
      }
      const emailValues = filledEmails.map((email) => email.value.trim().toLowerCase())
      if (new Set(emailValues).size !== emailValues.length) {
        next.emails = 'Duplicate email addresses are not allowed.'
      }
      const phoneValues = filledPhones.map((phone) => phone.value.trim().toLowerCase())
      if (new Set(phoneValues).size !== phoneValues.length) {
        next.phones = 'Duplicate phone numbers are not allowed.'
      }
    }

    setErrors(next)
    return Object.keys(next).length === 0
  }

  async function submit(event) {
    event.preventDefault()
    if (!validate()) return

    const cleaned = {
      firstName: draft.firstName.trim(),
      lastName: draft.lastName.trim(),
      title: draft.title.trim(),
      emails: draft.emails
        .filter((email) => email.value.trim())
        .map((email) => ({ value: email.value.trim(), label: email.label })),
      phones: draft.phones
        .filter((phone) => phone.value.trim())
        .map((phone) => ({ value: phone.value.trim(), label: phone.label })),
    }

    setSubmitting(true)
    setSubmitError('')
    try {
      await onSubmit(cleaned)
      onOpenChange(false)
    } catch (err) {
      setSubmitError(getErrorMessage(err, 'Failed to save contact.'))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-h-[90svh] overflow-y-auto sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>{mode === 'edit' ? 'Update contact' : 'New contact'}</DialogTitle>
          <DialogDescription>
            {mode === 'edit'
              ? 'Change any detail and save to update this contact.'
              : 'Every contact needs a name and at least one email address or phone number.'}
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={submit} noValidate>
          <FieldGroup>
            {submitError ? <FieldError>{submitError}</FieldError> : null}

            <div className="grid gap-4 sm:grid-cols-2">
              <Field data-invalid={errors.firstName ? true : undefined}>
                <FieldLabel htmlFor="contact-first">First name</FieldLabel>
                <Input
                  id="contact-first"
                  value={draft.firstName}
                  aria-invalid={errors.firstName ? true : undefined}
                  onChange={(event) => set('firstName', event.target.value)}
                />
                <FieldError>{errors.firstName}</FieldError>
              </Field>

              <Field data-invalid={errors.lastName ? true : undefined}>
                <FieldLabel htmlFor="contact-last">Last name</FieldLabel>
                <Input
                  id="contact-last"
                  value={draft.lastName}
                  aria-invalid={errors.lastName ? true : undefined}
                  onChange={(event) => set('lastName', event.target.value)}
                />
                <FieldError>{errors.lastName}</FieldError>
              </Field>
            </div>

            <Field>
              <FieldLabel htmlFor="contact-title">Title</FieldLabel>
              <Input
                id="contact-title"
                value={draft.title}
                placeholder="Product Designer"
                onChange={(event) => set('title', event.target.value)}
              />
            </Field>

            <FieldSeparator>Email addresses</FieldSeparator>

            <Field data-invalid={errors.emails ? true : undefined}>
              <div className="flex flex-col gap-2">
                {draft.emails.map((email, index) => (
                  <div key={email.id} className="flex items-center gap-2">
                    <Select
                      items={emailLabels}
                      value={email.label}
                      onValueChange={(value) =>
                        set(
                          'emails',
                          draft.emails.map((item, itemIndex) =>
                            itemIndex === index ? { ...item, label: value } : item,
                          ),
                        )
                      }
                    >
                      <SelectTrigger className="h-8 w-28 shrink-0">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectGroup>
                          {emailLabels.map((item) => (
                            <SelectItem key={item.value} value={item.value}>
                              {item.label}
                            </SelectItem>
                          ))}
                        </SelectGroup>
                      </SelectContent>
                    </Select>
                    <Input
                      aria-label={`Email address ${index + 1}`}
                      type="email"
                      placeholder="name@company.com"
                      value={email.value}
                      aria-invalid={errors.emails ? true : undefined}
                      onChange={(event) =>
                        set(
                          'emails',
                          draft.emails.map((item, itemIndex) =>
                            itemIndex === index ? { ...item, value: event.target.value } : item,
                          ),
                        )
                      }
                    />
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon-sm"
                      aria-label={`Remove email address ${index + 1}`}
                      onClick={() =>
                        set(
                          'emails',
                          draft.emails.filter((_, itemIndex) => itemIndex !== index),
                        )
                      }
                    >
                      <Trash2 />
                    </Button>
                  </div>
                ))}
              </div>
              <Button
                type="button"
                variant="outline"
                size="sm"
                className="w-fit rounded-full"
                onClick={() =>
                  set('emails', [...draft.emails, { id: createId(), label: 'PERSONAL', value: '' }])
                }
              >
                <Plus data-icon="inline-start" />
                Add email
              </Button>
              <FieldError>{errors.emails}</FieldError>
            </Field>

            <FieldSeparator>Phone numbers</FieldSeparator>

            <Field data-invalid={errors.phones ? true : undefined}>
              <div className="flex flex-col gap-2">
                {draft.phones.map((phone, index) => (
                  <div key={phone.id} className="flex items-center gap-2">
                    <Select
                      items={phoneLabels}
                      value={phone.label}
                      onValueChange={(value) =>
                        set(
                          'phones',
                          draft.phones.map((item, itemIndex) =>
                            itemIndex === index ? { ...item, label: value } : item,
                          ),
                        )
                      }
                    >
                      <SelectTrigger className="h-8 w-28 shrink-0">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectGroup>
                          {phoneLabels.map((item) => (
                            <SelectItem key={item.value} value={item.value}>
                              {item.label}
                            </SelectItem>
                          ))}
                        </SelectGroup>
                      </SelectContent>
                    </Select>
                    <Input
                      aria-label={`Phone number ${index + 1}`}
                      type="tel"
                      placeholder="+1 (415) 555-0134"
                      value={phone.value}
                      aria-invalid={errors.phones ? true : undefined}
                      onChange={(event) =>
                        set(
                          'phones',
                          draft.phones.map((item, itemIndex) =>
                            itemIndex === index ? { ...item, value: event.target.value } : item,
                          ),
                        )
                      }
                    />
                    <Button
                      type="button"
                      variant="ghost"
                      size="icon-sm"
                      aria-label={`Remove phone number ${index + 1}`}
                      onClick={() =>
                        set(
                          'phones',
                          draft.phones.filter((_, itemIndex) => itemIndex !== index),
                        )
                      }
                    >
                      <Trash2 />
                    </Button>
                  </div>
                ))}
              </div>
              <Button
                type="button"
                variant="outline"
                size="sm"
                className="w-fit rounded-full"
                onClick={() =>
                  set('phones', [...draft.phones, { id: createId(), label: 'WORK', value: '' }])
                }
              >
                <Plus data-icon="inline-start" />
                Add phone
              </Button>
              <FieldError>{errors.phones}</FieldError>
            </Field>
          </FieldGroup>

          <DialogFooter className="mt-6">
            <Button type="button" variant="ghost" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting ? <Spinner data-icon="inline-start" /> : null}
              {submitting ? 'Saving…' : mode === 'edit' ? 'Save changes' : 'Create contact'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
