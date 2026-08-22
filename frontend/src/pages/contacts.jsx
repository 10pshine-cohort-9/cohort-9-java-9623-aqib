import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Eye, MoreHorizontal, Pencil, Plus, Search, Trash2, UsersRound, X } from 'lucide-react'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Empty, EmptyContent, EmptyDescription, EmptyHeader, EmptyMedia, EmptyTitle } from '@/components/ui/empty'
import {
  InputGroup,
  InputGroupAddon,
  InputGroupButton,
  InputGroupInput,
} from '@/components/ui/input-group'
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination'
import { Spinner } from '@/components/ui/spinner'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { ContactFormDialog } from '@/components/app/contact-form-dialog'
import { DeleteContactDialog } from '@/components/app/delete-contact-dialog'
import { fullName, initials } from '@/lib/contacts'
import { listContacts, createContact, updateContact, deleteContact, getContact } from '@/services/contactApi'
import { getErrorMessage } from '@/services/api'
import { toast } from 'sonner'

const PAGE_SIZE = 8

export default function ContactsPage() {
  const [contacts, setContacts] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [search, setSearch] = useState('')
  const [debouncedSearch, setDebouncedSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [formOpen, setFormOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [pendingDelete, setPendingDelete] = useState(null)

  useEffect(() => {
    document.title = 'Contacts · Kith'
  }, [])

  const fetchContacts = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const data = await listContacts({ page, size: PAGE_SIZE, search: debouncedSearch })
      setContacts(data.content)
      setTotalPages(data.totalPages)
      setTotalElements(data.totalElements)
      // Clamp page if the last item on a page was deleted.
      if (data.content.length === 0 && page > 0 && data.totalPages < page + 1) {
        setPage(Math.max(0, data.totalPages - 1))
      }
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load contacts.'))
      setContacts([])
    } finally {
      setLoading(false)
    }
  }, [page, debouncedSearch])

  useEffect(() => {
    fetchContacts()
  }, [fetchContacts])

  // Debounce the search input and reset to the first page.
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search)
      setPage(0)
    }, 350)
    return () => clearTimeout(timer)
  }, [search])

  function openCreate() {
    setEditing(null)
    setFormOpen(true)
  }

  async function openEdit(contact) {
    // The list item omits emails/phones, so fetch the full record before editing
    // to avoid wiping the existing labeled contact methods on save.
    try {
      const full = await getContact(contact.id)
      setEditing(full)
      setFormOpen(true)
    } catch (err) {
      toast.error(getErrorMessage(err, 'Failed to load contact for editing.'))
    }
  }

  async function handleSubmit(draft) {
    if (editing) {
      await updateContact(editing.id, draft)
      toast.success('Contact updated', { description: fullName(draft) })
    } else {
      await createContact(draft)
      toast.success('Contact created', { description: fullName(draft) })
    }
    await fetchContacts()
  }

  async function handleDelete() {
    if (!pendingDelete) return
    await deleteContact(pendingDelete.id)
    toast.success('Contact deleted', { description: fullName(pendingDelete) })
    await fetchContacts()
  }

  const currentPage = page + 1
  const from = totalElements === 0 ? 0 : page * PAGE_SIZE + 1
  const to = Math.min(page * PAGE_SIZE + contacts.length, totalElements)

  return (
    <div className="flex min-h-svh flex-col">
      <header className="flex flex-col gap-4 border-b border-border px-6 py-6 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <p className="text-[10px] font-medium tracking-widest text-muted-foreground uppercase">
            Address book
          </p>
          <h1 className="mt-1.5 text-2xl font-semibold tracking-tight">Contacts</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            {totalElements} {totalElements === 1 ? 'person' : 'people'}
            {debouncedSearch ? ` matching “${debouncedSearch}”` : ''}
          </p>
        </div>

        <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
          <InputGroup className="h-9 sm:w-72">
            <InputGroupAddon>
              <Search />
            </InputGroupAddon>
            <InputGroupInput
              placeholder="Search by name…"
              aria-label="Search contacts"
              value={search}
              onChange={(event) => setSearch(event.target.value)}
            />
            {search ? (
              <InputGroupAddon align="inline-end">
                <InputGroupButton size="icon-xs" aria-label="Clear search" onClick={() => setSearch('')}>
                  <X />
                </InputGroupButton>
              </InputGroupAddon>
            ) : null}
          </InputGroup>

          <Button size="lg" className="h-9 rounded-full" onClick={openCreate}>
            <Plus data-icon="inline-start" />
            New contact
          </Button>
        </div>
      </header>

      <div className="flex-1 px-6 py-6">
        {error ? (
          <div className="rounded-xl border border-destructive/30 bg-destructive/5 px-4 py-3 text-sm text-destructive">
            {error}
          </div>
        ) : loading ? (
          <div className="flex items-center justify-center py-20 text-muted-foreground">
            <Spinner className="size-5" />
            <span className="ml-2 text-sm">Loading contacts…</span>
          </div>
        ) : contacts.length === 0 ? (
          <Empty className="rounded-xl border border-dashed border-border py-20">
            <EmptyHeader>
              <EmptyMedia variant="icon">
                <UsersRound />
              </EmptyMedia>
              <EmptyTitle>No contacts found</EmptyTitle>
              <EmptyDescription>
                {debouncedSearch
                  ? 'Nothing matches that search. Try a different name.'
                  : 'Your address book is empty. Add your first contact to get started.'}
              </EmptyDescription>
            </EmptyHeader>
            <EmptyContent>
              <Button className="rounded-full" onClick={openCreate}>
                <Plus data-icon="inline-start" />
                New contact
              </Button>
            </EmptyContent>
          </Empty>
        ) : (
          <div className="overflow-hidden rounded-xl border border-border bg-card">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Name</TableHead>
                  <TableHead className="hidden sm:table-cell">Title</TableHead>
                  <TableHead className="w-12 text-right">
                    <span className="sr-only">Actions</span>
                  </TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {contacts.map((contact) => (
                  <TableRow key={contact.id}>
                    <TableCell>
                      <div className="flex min-w-0 items-center gap-3">
                        <Avatar className="size-8">
                          <AvatarFallback className="text-[10px]">{initials(contact)}</AvatarFallback>
                        </Avatar>
                        <div className="min-w-0">
                          <Link
                            to={`/contacts/${contact.id}`}
                            className="truncate text-sm font-medium hover:underline"
                          >
                            {fullName(contact)}
                          </Link>
                        </div>
                      </div>
                    </TableCell>
                    <TableCell className="hidden text-sm text-muted-foreground sm:table-cell">
                      {contact.title || '—'}
                    </TableCell>
                    <TableCell className="text-right">
                      <DropdownMenu>
                        <DropdownMenuTrigger
                          render={
                            <Button
                              variant="ghost"
                              size="icon-sm"
                              aria-label={`Actions for ${fullName(contact)}`}
                            >
                              <MoreHorizontal />
                            </Button>
                          }
                        />
                        <DropdownMenuContent align="end">
                          <DropdownMenuGroup>
                            <DropdownMenuItem
                              render={
                                <Link to={`/contacts/${contact.id}`}>
                                  <Eye />
                                  View profile
                                </Link>
                              }
                            />
                            <DropdownMenuItem onClick={() => openEdit(contact)}>
                              <Pencil />
                              Update
                            </DropdownMenuItem>
                          </DropdownMenuGroup>
                          <DropdownMenuSeparator />
                          <DropdownMenuGroup>
                            <DropdownMenuItem
                              variant="destructive"
                              onClick={() => setPendingDelete(contact)}
                            >
                              <Trash2 />
                              Delete
                            </DropdownMenuItem>
                          </DropdownMenuGroup>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>

            <div className="flex flex-col items-center justify-between gap-3 border-t border-border px-4 py-3 sm:flex-row">
              <p className="text-xs text-muted-foreground">
                {totalElements > 0 ? `Showing ${from}–${to} of ${totalElements}` : 'No contacts'}
              </p>
              {totalPages > 1 ? (
                <Pagination className="mx-0 w-auto">
                  <PaginationContent>
                    <PaginationItem>
                      <PaginationPrevious
                        href="#"
                        aria-disabled={page === 0}
                        className={page === 0 ? 'pointer-events-none opacity-50' : undefined}
                        onClick={(event) => {
                          event.preventDefault()
                          setPage(Math.max(0, page - 1))
                        }}
                      />
                    </PaginationItem>
                    {Array.from({ length: totalPages }).map((_, index) => (
                      <PaginationItem key={index}>
                        <PaginationLink
                          href="#"
                          isActive={currentPage === index + 1}
                          onClick={(event) => {
                            event.preventDefault()
                            setPage(index)
                          }}
                        >
                          {index + 1}
                        </PaginationLink>
                      </PaginationItem>
                    ))}
                    <PaginationItem>
                      <PaginationNext
                        href="#"
                        aria-disabled={page >= totalPages - 1}
                        className={page >= totalPages - 1 ? 'pointer-events-none opacity-50' : undefined}
                        onClick={(event) => {
                          event.preventDefault()
                          setPage(Math.min(totalPages - 1, page + 1))
                        }}
                      />
                    </PaginationItem>
                  </PaginationContent>
                </Pagination>
              ) : null}
            </div>
          </div>
        )}
      </div>

      <ContactFormDialog
        open={formOpen}
        onOpenChange={setFormOpen}
        contact={editing}
        onSubmit={handleSubmit}
      />

      <DeleteContactDialog
        contact={pendingDelete}
        open={pendingDelete !== null}
        onOpenChange={(open) => {
          if (!open) setPendingDelete(null)
        }}
        onConfirm={handleDelete}
      />
    </div>
  )
}
