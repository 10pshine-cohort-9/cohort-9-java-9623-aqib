import axios from 'axios'

const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
})

const TOKEN_KEY = 'cms_token'
const REFRESH_KEY = 'cms_refresh'
const USER_KEY = 'cms_user'

export const tokenStore = {
  getToken: () => localStorage.getItem(TOKEN_KEY),
  getRefreshToken: () => localStorage.getItem(REFRESH_KEY),
  getUser: () => {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  },
  set: (token, refreshToken, user) => {
    localStorage.setItem(TOKEN_KEY, token)
    if (refreshToken) localStorage.setItem(REFRESH_KEY, refreshToken)
    if (user) localStorage.setItem(USER_KEY, JSON.stringify(user))
  },
  setTokens: (token, refreshToken) => {
    localStorage.setItem(TOKEN_KEY, token)
    if (refreshToken) localStorage.setItem(REFRESH_KEY, refreshToken)
  },
  clear: () => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(REFRESH_KEY)
    localStorage.removeItem(USER_KEY)
  },
}

// Attach the access token to every authenticated request (skip public auth routes).
api.interceptors.request.use((config) => {
  const token = tokenStore.getToken()
  const isAuthRoute = config.url?.startsWith('/auth/')
  if (token && !isAuthRoute) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let refreshPromise = null

async function refreshAccessToken() {
  const refreshToken = tokenStore.getRefreshToken()
  if (!refreshToken) return null
  if (!refreshPromise) {
    refreshPromise = axios
      .post(`${API_BASE}/auth/refresh`, { refreshToken })
      .finally(() => {
        refreshPromise = null
      })
  }
  const { data } = await refreshPromise
  const payload = data.data
  tokenStore.setTokens(payload.token, payload.refreshToken)
  return payload.token
}

function redirectToLogin() {
  tokenStore.clear()
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}

// Auto-refresh on 401; log out only when the refresh token is unusable.
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const status = error.response?.status
    const original = error.config

    if (status === 401 && original && !original._retry && !original.url?.startsWith('/auth/')) {
      original._retry = true
      try {
        const newToken = await refreshAccessToken()
        if (newToken) {
          original.headers.Authorization = `Bearer ${newToken}`
          return api(original)
        }
      } catch {
        // refresh failed — fall through to logout
      }
      redirectToLogin()
    }
    return Promise.reject(error)
  },
)

// Extracts a human-readable message from any axios error.
export function getErrorMessage(err, fallback = 'Something went wrong. Please try again.') {
  const data = err.response?.data
  if (!data) {
    if (err.request) return 'No response from the server. Check your connection.'
    return fallback
  }
  if (typeof data === 'string') return data
  if (data.validationErrors && typeof data.validationErrors === 'object') {
    const entries = Object.entries(data.validationErrors)
    if (entries.length) {
      return entries.map(([field, message]) => `${field}: ${message}`).join(' · ')
    }
  }
  if (data.message) return data.message
  if (Array.isArray(data.errors) && data.errors.length) {
    return data.errors.map((e) => (e.field ? `${e.field}: ${e.message}` : e.message)).join(' ')
  }
  return fallback
}

export default api
