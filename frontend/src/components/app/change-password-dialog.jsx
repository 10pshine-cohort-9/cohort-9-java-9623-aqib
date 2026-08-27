import { useMemo, useState } from 'react'
import { Eye, EyeOff, KeyRound } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Field, FieldError, FieldGroup, FieldLabel } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import {
  InputGroup,
  InputGroupAddon,
  InputGroupButton,
  InputGroupInput,
} from '@/components/ui/input-group'
import { Spinner } from '@/components/ui/spinner'
import { changePassword } from '@/services/authApi'
import { getErrorMessage } from '@/services/api'

const MIN_LENGTH = 6

export function ChangePasswordDialog({ open, onOpenChange, onChanged }) {
  const [current, setCurrent] = useState('')
  const [next, setNext] = useState('')
  const [confirm, setConfirm] = useState('')
  const [visible, setVisible] = useState(false)
  const [errors, setErrors] = useState({})
  const [submitError, setSubmitError] = useState('')
  const [pending, setPending] = useState(false)

  const strength = useMemo(() => {
    let score = 0
    if (next.length >= MIN_LENGTH) score += 1
    if (/[A-Z]/.test(next)) score += 1
    if (/\d/.test(next)) score += 1
    if (/[^A-Za-z0-9]/.test(next)) score += 1
    return score
  }, [next])

  const strengthLabel = ['Too short', 'Weak', 'Fair', 'Good', 'Strong'][strength]

  function resetFields() {
    setCurrent('')
    setNext('')
    setConfirm('')
    setVisible(false)
    setErrors({})
    setSubmitError('')
  }

  function validate() {
    const found = {}
    if (!current) found.current = 'Enter your current password.'
    if (next.length < MIN_LENGTH) found.next = `Use at least ${MIN_LENGTH} characters.`
    else if (next === current) found.next = 'Choose a password you have not used before.'
    if (confirm !== next) found.confirm = 'Passwords do not match.'
    setErrors(found)
    return Object.keys(found).length === 0
  }

  async function onSubmit(event) {
    event.preventDefault()
    setSubmitError('')
    if (!validate()) return
    setPending(true)
    try {
      await changePassword({ currentPassword: current, newPassword: next })
      resetFields()
      onOpenChange(false)
      onChanged?.()
    } catch (err) {
      setSubmitError(getErrorMessage(err, 'Failed to change password.'))
    } finally {
      setPending(false)
    }
  }

  return (
    <Dialog
      open={open}
      onOpenChange={(value) => {
        if (!value) resetFields()
        onOpenChange(value)
      }}
    >
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Change password</DialogTitle>
          <DialogDescription>
            Choose a new password of at least {MIN_LENGTH} characters.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={onSubmit} noValidate>
          <FieldGroup>
            {submitError ? <FieldError>{submitError}</FieldError> : null}

            <Field data-invalid={errors.current ? true : undefined}>
              <FieldLabel htmlFor="currentPassword">Current password</FieldLabel>
              <InputGroup className="h-10">
                <InputGroupInput
                  id="currentPassword"
                  type={visible ? 'text' : 'password'}
                  autoComplete="current-password"
                  placeholder="••••••••"
                  value={current}
                  aria-invalid={errors.current ? true : undefined}
                  onChange={(event) => setCurrent(event.target.value)}
                />
                <InputGroupAddon align="inline-end">
                  <InputGroupButton
                    size="icon-xs"
                    aria-label={visible ? 'Hide password' : 'Show password'}
                    onClick={() => setVisible((value) => !value)}
                  >
                    {visible ? <EyeOff /> : <Eye />}
                  </InputGroupButton>
                </InputGroupAddon>
              </InputGroup>
              <FieldError>{errors.current}</FieldError>
            </Field>

            <Field data-invalid={errors.next ? true : undefined}>
              <FieldLabel htmlFor="newPassword">New password</FieldLabel>
              <Input
                id="newPassword"
                type={visible ? 'text' : 'password'}
                autoComplete="new-password"
                className="h-10"
                placeholder="••••••••"
                value={next}
                aria-invalid={errors.next ? true : undefined}
                onChange={(event) => setNext(event.target.value)}
              />
              {next.length > 0 ? (
                <div className="flex items-center gap-2.5 pt-1">
                  <div className="flex flex-1 gap-1" aria-hidden="true">
                    {[0, 1, 2, 3].map((bar) => (
                      <span
                        key={bar}
                        className={
                          bar < strength
                            ? 'h-1 flex-1 rounded-full bg-primary'
                            : 'h-1 flex-1 rounded-full bg-border'
                        }
                      />
                    ))}
                  </div>
                  <span className="text-[10px] font-medium tracking-widest text-muted-foreground uppercase">
                    {strengthLabel}
                  </span>
                </div>
              ) : null}
              <FieldError>{errors.next}</FieldError>
            </Field>

            <Field data-invalid={errors.confirm ? true : undefined}>
              <FieldLabel htmlFor="confirmPassword">Confirm new password</FieldLabel>
              <Input
                id="confirmPassword"
                type={visible ? 'text' : 'password'}
                autoComplete="new-password"
                className="h-10"
                placeholder="••••••••"
                value={confirm}
                aria-invalid={errors.confirm ? true : undefined}
                onChange={(event) => setConfirm(event.target.value)}
              />
              <FieldError>{errors.confirm}</FieldError>
            </Field>
          </FieldGroup>

          <DialogFooter className="mt-6">
            <Button type="button" variant="ghost" onClick={resetFields}>
              Reset
            </Button>
            <Button type="button" variant="outline" onClick={() => { resetFields(); onOpenChange(false) }}>
              Cancel
            </Button>
            <Button type="submit" disabled={pending}>
              {pending ? <Spinner data-icon="inline-start" /> : <KeyRound data-icon="inline-start" />}
              {pending ? 'Updating…' : 'Update password'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
