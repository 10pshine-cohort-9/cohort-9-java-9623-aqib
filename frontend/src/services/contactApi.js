import api from './api'

export async function listContacts({ page = 0, size = 10, search = '' } = {}) {
  const params = { page, size }
  if (search) params.search = search
  const { data } = await api.get('/contacts', { params })
  return data.data
}

export async function getContact(id) {
  const { data } = await api.get(`/contacts/${id}`)
  return data.data
}

export async function createContact(payload) {
  const { data } = await api.post('/contacts', payload)
  return data.data
}

export async function updateContact(id, payload) {
  const { data } = await api.put(`/contacts/${id}`, payload)
  return data.data
}

export async function deleteContact(id) {
  const { data } = await api.delete(`/contacts/${id}`)
  return data
}
