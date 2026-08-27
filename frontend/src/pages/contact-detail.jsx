import { useCallback, useEffect, useRef, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { ArrowLeft, Mail, Pencil, Phone, Trash2 } from 'lucide-react'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Spinner } from '@/components/ui/spinner'
import { ContactFormDialog } from '@/components/app/contact-form-dialog'
import { DeleteContactDialog } from '@/components/app/delete-contact-dialog'
import { fullName, initials, labelDisplay } from '@/lib/contacts'
import { getContact, updateContact, deleteContact } from '@/services/contactApi'
import { getErrorMessage } from '@/services/api'
import { toast } from 'sonner'

function formatDate(value) {
  if (!value) return '—'
  const date = new Date(value)
  return date.toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  })
}

export default function ContactDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const fetchId = useRef(0)
  const [contact, setContact] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [formOpen, setFormOpen] = useState(false)
  const [pendingDelete, setPendingDelete] = useState(false)

  useEffect(() => {
    document.title = 'Contact · Kith'
  }, [])

  const fetchContact = useCallback(async () => {
    const requestId = ++fetchId.current
    setLoading(true)
    setError('')
    try {
      const data = await getContact(id)
      if (requestId !== fetchId.current) return
      setContact(data)
    } catch (err) {
      if (requestId !== fetchId.current) return
      setError(getErrorMessage(err, 'Failed to load contact.'))
    } finally {
      if (requestId === fetchId.current) setLoading(false)
    }
  }, [id])

  useEffect(() => {
    fetchContact()
  }, [fetchContact])

  async function handleSubmit(draft) {
    await updateContact(id, draft)
    toast.success('Contact updated', { description: fullName(draft) })
    await fetchContact()
  }

  async function handleDelete() {
    await deleteContact(id)
    toast.success('Contact deleted')
    navigate('/contacts', { replace: true })
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20 text-muted-foreground">
        <Spinner className="size-5" />
        <span className="ml-2 text-sm">Loading contact…</span>
      </div>
    )
  }

  if (error || !contact) {
    return (
      <div className="mx-auto max-w-2xl px-6 py-12">
        <Button
          variant="ghost"
          size="sm"
          className="-ml-2 w-fit text-muted-foreground"
          nativeButton={false}
          render={
            <Link to="/contacts">
              <ArrowLeft data-icon="inline-start" />
              Back to contacts
            </Link>
          }
        />
        <div className="mt-6 rounded-xl border border-destructive/30 bg-destructive/5 px-4 py-3 text-sm text-destructive">
          {error || 'Contact not found.'}
        </div>
      </div>
    )
  }

  return (
    <div className="mx-auto flex max-w-3xl flex-col gap-8 px-4 py-8 sm:px-8 lg:py-12">
      <Button
        variant="ghost"
        size="sm"
        className="-ml-2 w-fit text-muted-foreground"
        nativeButton={false}
        render={
          <Link to="/contacts">
            <ArrowLeft data-icon="inline-start" />
            Back to contacts
          </Link>
        }
      />

      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex items-center gap-4">
          <Avatar className="size-16 rounded-xl">
            <AvatarFallback className="rounded-xl bg-primary text-base text-primary-foreground">
              {initials(contact)}
            </AvatarFallback>
          </Avatar>
          <div className="flex flex-col gap-1">
            <h1 className="font-serif text-2xl tracking-tight sm:text-3xl">{fullName(contact)}</h1>
            {contact.title ? (
              <p className="text-sm text-muted-foreground">{contact.title}</p>
            ) : null}
          </div>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" className="rounded-full" onClick={() => setFormOpen(true)}>
            <Pencil data-icon="inline-start" />
            Edit
          </Button>
          <Button
            variant="ghost"
            className="rounded-full text-destructive hover:bg-destructive/10"
            onClick={() => setPendingDelete(true)}
          >
            <Trash2 data-icon="inline-start" />
            Delete
          </Button>
        </div>
      </div>

      <div className="grid gap-6 sm:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-base">
              <Mail className="size-4 text-muted-foreground" />
              Email addresses
            </CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col gap-3">
            {contact.emails?.length ? (
              contact.emails.map((email) => (
                <div key={email.id} className="flex items-center justify-between gap-3">
                  <a
                    href={`mailto:${email.value}`}
                    className="text-sm hover:underline"
                  >
                    {email.value}
                  </a>
                  <Badge variant="secondary">{labelDisplay(email.label)}</Badge>
                </div>
              ))
            ) : (
              <p className="text-sm text-muted-foreground">No email addresses.</p>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-base">
              <Phone className="size-4 text-muted-foreground" />
              Phone numbers
            </CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col gap-3">
            {contact.phones?.length ? (
              contact.phones.map((phone) => (
                <div key={phone.id} className="flex items-center justify-between gap-3">
                  <a href={`tel:${phone.value}`} className="text-sm hover:underline">
                    {phone.value}
                  </a>
                  <Badge variant="secondary">{labelDisplay(phone.label)}</Badge>
                </div>
              ))
            ) : (
              <p className="text-sm text-muted-foreground">No phone numbers.</p>
            )}
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Details</CardTitle>
        </CardHeader>
        <CardContent>
          <dl className="grid gap-3 sm:grid-cols-2">
            <div>
              <dt className="text-[10px] font-medium tracking-widest text-muted-foreground uppercase">
                Created
              </dt>
              <dd className="text-sm">{formatDate(contact.createdAt)}</dd>
            </div>
            <div>
              <dt className="text-[10px] font-medium tracking-widest text-muted-foreground uppercase">
                Last updated
              </dt>
              <dd className="text-sm">{formatDate(contact.updatedAt)}</dd>
            </div>
          </dl>
        </CardContent>
      </Card>

      <ContactFormDialog
        open={formOpen}
        onOpenChange={setFormOpen}
        contact={contact}
        onSubmit={handleSubmit}
      />

      <DeleteContactDialog
        contact={contact}
        open={pendingDelete}
        onOpenChange={setPendingDelete}
        onConfirm={handleDelete}
      />
    </div>
  )
}
