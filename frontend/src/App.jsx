import { Suspense } from 'react'
import { Routes, Route } from 'react-router-dom'
import { AppShell } from '@/components/app/app-shell'
import { RequireAuth } from '@/components/app/require-auth'
import { Toaster } from '@/components/ui/sonner'
import HomePage from '@/pages/home'
import LoginPage from '@/pages/login'
import RegisterPage from '@/pages/register'
import ContactsPage from '@/pages/contacts'
import ContactDetailPage from '@/pages/contact-detail'
import ProfilePage from '@/pages/profile'

function AppLayout() {
  return (
    <RequireAuth>
      <Suspense fallback={null}>
        <AppShell>
          <Routes>
            <Route path="/contacts" element={<ContactsPage />} />
            <Route path="/contacts/:id" element={<ContactDetailPage />} />
            <Route path="/profile" element={<ProfilePage />} />
          </Routes>
        </AppShell>
      </Suspense>
    </RequireAuth>
  )
}

export default function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/*" element={<AppLayout />} />
      </Routes>
      <Toaster position="bottom-right" />
    </>
  )
}
