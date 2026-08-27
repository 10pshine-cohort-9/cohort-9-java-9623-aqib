import { useState } from 'react'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import { Spinner } from '@/components/ui/spinner'
import { fullName } from '@/lib/contacts'

export function DeleteContactDialog({ contact, open, onOpenChange, onConfirm }) {
  const [submitting, setSubmitting] = useState(false)

  async function handleConfirm() {
    if (!contact) return
    setSubmitting(true)
    try {
      await onConfirm()
      onOpenChange(false)
    } catch {
      // parent surfaces the error via toast; keep the dialog open for retry
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AlertDialog open={open} onOpenChange={(value) => (submitting ? undefined : onOpenChange(value))}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Delete {contact ? fullName(contact) : 'this contact'}?</AlertDialogTitle>
          <AlertDialogDescription>
            This permanently removes the contact and all of its labeled email addresses and phone
            numbers from your address book. This action cannot be undone.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel disabled={submitting}>Cancel</AlertDialogCancel>
          <AlertDialogAction
            className="bg-destructive/10 text-destructive hover:bg-destructive/20"
            onClick={handleConfirm}
            disabled={submitting}
          >
            {submitting ? <Spinner data-icon="inline-start" /> : null}
            {submitting ? 'Deleting…' : 'Delete contact'}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  )
}
