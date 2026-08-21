import { useEffect } from 'react'
import { Link } from 'react-router-dom'
import { AuthShell } from '@/components/auth/auth-shell'
import { RegisterForm } from '@/components/auth/register-form'

export default function RegisterPage() {
  useEffect(() => {
    document.title = 'Create account · Kith'
  }, [])

  return (
    <AuthShell
      title="Create your Kith account"
      description="Register with an email address or a phone number. It takes about thirty seconds."
      footer={
        <>
          Already have an account?{' '}
          <Link to="/login" className="font-medium text-foreground underline underline-offset-4">
            Log in
          </Link>
        </>
      }
    >
      <RegisterForm />
    </AuthShell>
  )
}
