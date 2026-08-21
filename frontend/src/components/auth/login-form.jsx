import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Eye, EyeOff, LogIn } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Field, FieldError, FieldGroup, FieldLabel } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import {
  InputGroup,
  InputGroupAddon,
  InputGroupButton,
  InputGroupInput,
} from '@/components/ui/input-group'
import { Spinner } from '@/components/ui/spinner'
import { useAuth } from '@/context/AuthContext'
import { login as loginApi } from '@/services/authApi'
import { getErrorMessage } from '@/services/api'

export function LoginForm() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [identifier, setIdentifier] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [errors, setErrors] = useState({})
  const [serverError, setServerError] = useState('')
  const [pending, setPending] = useState(false)

  function validate() {
    const next = {}
    const isEmail = /^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(identifier)
    const isPhone = /^\+?[\d\s()-]{7,}$/.test(identifier)

    if (!identifier.trim()) {
      next.identifier = 'Enter the email or phone number you registered with.'
    } else if (!isEmail && !isPhone) {
      next.identifier = 'That does not look like an email address or phone number.'
    }

    if (!password) {
      next.password = 'Enter your password.'
    }

    setErrors(next)
    return Object.keys(next).length === 0
  }

  async function onSubmit(event) {
    event.preventDefault()
    setServerError('')
    if (!validate()) return

    setPending(true)
    try {
      const authData = await loginApi({ identifier: identifier.trim(), password })
      login(authData)
      navigate('/contacts', { replace: true })
    } catch (err) {
      setServerError(getErrorMessage(err, 'Login failed. Please try again.'))
    } finally {
      setPending(false)
    }
  }

  return (
    <form onSubmit={onSubmit} noValidate>
      <FieldGroup>
        {serverError ? <FieldError>{serverError}</FieldError> : null}

        <Field data-invalid={errors.identifier ? true : undefined}>
          <FieldLabel htmlFor="identifier">Email or phone number</FieldLabel>
          <Input
            id="identifier"
            name="identifier"
            autoComplete="username"
            className="h-10"
            value={identifier}
            aria-invalid={errors.identifier ? true : undefined}
            onChange={(event) => setIdentifier(event.target.value)}
          />
          <FieldError>{errors.identifier}</FieldError>
        </Field>

        <Field data-invalid={errors.password ? true : undefined}>
          <div className="flex items-center justify-between">
            <FieldLabel htmlFor="password">Password</FieldLabel>
          </div>
          <InputGroup className="h-10">
            <InputGroupInput
              id="password"
              name="password"
              type={showPassword ? 'text' : 'password'}
              autoComplete="current-password"
              placeholder="••••••••"
              value={password}
              aria-invalid={errors.password ? true : undefined}
              onChange={(event) => setPassword(event.target.value)}
            />
            <InputGroupAddon align="inline-end">
              <InputGroupButton
                size="icon-xs"
                aria-label={showPassword ? 'Hide password' : 'Show password'}
                onClick={() => setShowPassword((value) => !value)}
              >
                {showPassword ? <EyeOff /> : <Eye />}
              </InputGroupButton>
            </InputGroupAddon>
          </InputGroup>
          <FieldError>{errors.password}</FieldError>
        </Field>

        <Button type="submit" size="lg" className="h-10 w-full rounded-full" disabled={pending}>
          {pending ? <Spinner data-icon="inline-start" /> : <LogIn data-icon="inline-start" />}
          {pending ? 'Signing in…' : 'Log in'}
        </Button>
      </FieldGroup>
    </form>
  )
}
