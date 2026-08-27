import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { UserPlus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Field, FieldDescription, FieldError, FieldGroup, FieldLabel } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { Spinner } from '@/components/ui/spinner'
import { useAuth } from '@/context/AuthContext'
import { register as registerApi } from '@/services/authApi'
import { getErrorMessage } from '@/services/api'

const MIN_LENGTH = 6

export function RegisterForm() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [values, setValues] = useState({
    firstName: '',
    lastName: '',
    identifier: '',
    password: '',
    confirm: '',
  })
  const [errors, setErrors] = useState({})
  const [serverError, setServerError] = useState('')
  const [pending, setPending] = useState(false)

  function update(key) {
    return (event) => setValues((current) => ({ ...current, [key]: event.target.value }))
  }

  function validate() {
    const next = {}
    const isEmail = /^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(values.identifier)
    const isPhone = /^\+?[\d\s()-]{7,}$/.test(values.identifier)

    if (!values.firstName.trim()) next.firstName = 'First name is required.'
    if (!values.lastName.trim()) next.lastName = 'Last name is required.'
    if (!values.identifier.trim()) {
      next.identifier = 'Enter an email address or phone number.'
    } else if (!isEmail && !isPhone) {
      next.identifier = 'Use a valid email address or phone number.'
    }
    if (values.password.length < MIN_LENGTH) next.password = `Use at least ${MIN_LENGTH} characters.`
    if (values.confirm !== values.password) next.confirm = 'Passwords do not match.'

    setErrors(next)
    return Object.keys(next).length === 0
  }

  async function onSubmit(event) {
    event.preventDefault()
    setServerError('')
    if (!validate()) return

    const isEmail = /^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(values.identifier)
    const payload = {
      firstName: values.firstName.trim(),
      lastName: values.lastName.trim(),
      password: values.password,
      email: isEmail ? values.identifier.trim() : null,
      phone: !isEmail ? values.identifier.trim() : null,
    }

    setPending(true)
    try {
      const authData = await registerApi(payload)
      login(authData)
      navigate('/contacts', { replace: true })
    } catch (err) {
      setServerError(getErrorMessage(err, 'Registration failed. Please try again.'))
    } finally {
      setPending(false)
    }
  }

  return (
    <form onSubmit={onSubmit} noValidate>
      <FieldGroup>
        {serverError ? <FieldError>{serverError}</FieldError> : null}

        <div className="grid gap-4 sm:grid-cols-2">
          <Field data-invalid={errors.firstName ? true : undefined}>
            <FieldLabel htmlFor="firstName">First name</FieldLabel>
            <Input
              id="firstName"
              className="h-10"
              value={values.firstName}
              aria-invalid={errors.firstName ? true : undefined}
              onChange={update('firstName')}
            />
            <FieldError>{errors.firstName}</FieldError>
          </Field>

          <Field data-invalid={errors.lastName ? true : undefined}>
            <FieldLabel htmlFor="lastName">Last name</FieldLabel>
            <Input
              id="lastName"
              className="h-10"
              value={values.lastName}
              aria-invalid={errors.lastName ? true : undefined}
              onChange={update('lastName')}
            />
            <FieldError>{errors.lastName}</FieldError>
          </Field>
        </div>

        <Field data-invalid={errors.identifier ? true : undefined}>
          <FieldLabel htmlFor="identifier">Email or phone number</FieldLabel>
          <Input
            id="identifier"
            className="h-10"
            autoComplete="username"
            value={values.identifier}
            aria-invalid={errors.identifier ? true : undefined}
            onChange={update('identifier')}
          />
          <FieldDescription>Either one works — you will log in with it.</FieldDescription>
          <FieldError>{errors.identifier}</FieldError>
        </Field>

        <Field data-invalid={errors.password ? true : undefined}>
          <FieldLabel htmlFor="password">Password</FieldLabel>
          <Input
            id="password"
            type="password"
            className="h-10"
            autoComplete="new-password"
            value={values.password}
            aria-invalid={errors.password ? true : undefined}
            onChange={update('password')}
          />
          <FieldError>{errors.password}</FieldError>
        </Field>

        <Field data-invalid={errors.confirm ? true : undefined}>
          <FieldLabel htmlFor="confirm">Confirm password</FieldLabel>
          <Input
            id="confirm"
            type="password"
            className="h-10"
            autoComplete="new-password"
            value={values.confirm}
            aria-invalid={errors.confirm ? true : undefined}
            onChange={update('confirm')}
          />
          <FieldError>{errors.confirm}</FieldError>
        </Field>

        <Button type="submit" size="lg" className="h-10 w-full rounded-full" disabled={pending}>
          {pending ? <Spinner data-icon="inline-start" /> : <UserPlus data-icon="inline-start" />}
          {pending ? 'Creating account…' : 'Create account'}
        </Button>
      </FieldGroup>
    </form>
  )
}
