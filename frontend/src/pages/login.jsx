import { useEffect } from 'react'
import { Link } from 'react-router-dom'
import { AuthShell } from '@/components/auth/auth-shell'
import { LoginForm } from '@/components/auth/login-form'

export default function LoginPage() {
  useEffect(() => {
    document.title = 'Log in · Kith'
  }, [])

  return (
    <AuthShell
      eyebrow="Welcome back"
      title="Log in to your address book"
      description="Use the email address or phone number you registered with."
      footer={
        <>
          New to Kith?{' '}
          <Link to="/register" className="font-medium text-foreground underline underline-offset-4">
            Create an account
          </Link>
        </>
      }
    >
      <LoginForm />
    </AuthShell>
  )
}
