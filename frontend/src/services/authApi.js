import api from './api'

export async function register(payload) {
  const { data } = await api.post('/auth/register', payload)
  return data.data
}

export async function login(payload) {
  const { data } = await api.post('/auth/login', payload)
  return data.data
}

export async function getProfile() {
  const { data } = await api.get('/users/me')
  return data.data
}

export async function changePassword(payload) {
  const { data } = await api.put('/users/me/password', payload)
  return data
}

export async function logout(refreshToken) {
  try {
    await api.post('/users/logout', refreshToken ? { refreshToken } : {})
  } catch {
    // ignore network/server errors on logout — the client clears the session anyway
  }
}
