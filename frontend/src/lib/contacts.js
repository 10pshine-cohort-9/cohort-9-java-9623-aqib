// Label options mirror the backend enums (EmailLabel / PhoneLabel) exactly.
export const emailLabels = [
  { value: 'WORK', label: 'Work' },
  { value: 'PERSONAL', label: 'Personal' },
  { value: 'OTHER', label: 'Other' },
]

export const phoneLabels = [
  { value: 'WORK', label: 'Work' },
  { value: 'HOME', label: 'Home' },
  { value: 'PERSONAL', label: 'Personal' },
  { value: 'OTHER', label: 'Other' },
]

export function labelDisplay(value) {
  if (!value) return ''
  const label = [...emailLabels, ...phoneLabels].find((item) => item.value === value)
  return label ? label.label : value.charAt(0) + value.slice(1).toLowerCase()
}

export function fullName(contact) {
  return `${contact.firstName ?? ''} ${contact.lastName ?? ''}`.trim()
}

export function initials(contact) {
  const first = contact.firstName?.charAt(0) ?? ''
  const last = contact.lastName?.charAt(0) ?? ''
  return `${first}${last}`.toUpperCase() || '?'
}

export function primaryEmail(contact) {
  return contact.emails?.[0]?.value ?? '—'
}

export function primaryPhone(contact) {
  return contact.phones?.[0]?.value ?? '—'
}

export function createId() {
  return Math.random().toString(36).slice(2, 10)
}

// Blank draft used by the create form. Only fields the backend persists.
export const emptyContact = {
  firstName: '',
  lastName: '',
  title: '',
  emails: [{ id: 'new-e1', label: 'WORK', value: '' }],
  phones: [{ id: 'new-p1', label: 'WORK', value: '' }],
}
