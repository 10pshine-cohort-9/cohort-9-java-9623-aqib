import { createContext, useContext, useEffect, useState } from 'react'
import { tokenStore } from '../services/api'
import { getProfile, logout as apiLogout } from '../services/authApi'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => tokenStore.getUser())
  const [token, setToken] = useState(() => tokenStore.getToken())
  const [loading, setLoading] = useState(() => Boolean(tokenStore.getToken()))

  // Validate the stored token and refresh the user profile once on first load.
  useEffect(() => {
    let active = true
    const storedToken = tokenStore.getToken()
    if (!storedToken) {
      setLoading(false)
      return
    }
    getProfile()
      .then((profile) => {
        if (active) {
          setUser(profile)
          tokenStore.set(storedToken, tokenStore.getRefreshToken(), profile)
        }
      })
      .catch(() => {
        if (active) {
          tokenStore.clear()
          setUser(null)
          setToken(null)
        }
      })
      .finally(() => {
        if (active) setLoading(false)
      })
    return () => {
      active = false
    }
  }, [])

  function login(authData) {
    tokenStore.set(authData.token, authData.refreshToken, authData.user)
    setToken(authData.token)
    setUser(authData.user)
  }

  async function logout() {
    try {
      await apiLogout(tokenStore.getRefreshToken())
    } finally {
      tokenStore.clear()
      setToken(null)
      setUser(null)
    }
  }

  const value = {
    user,
    token,
    loading,
    isAuthenticated: Boolean(token),
    login,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within an AuthProvider')
  return ctx
}
