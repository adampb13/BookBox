import React from 'react'
import { render, screen, waitFor, fireEvent } from '@testing-library/react'
import { vi } from 'vitest'
import Admin from '../pages/Admin'

beforeEach(() => {
  // @ts-ignore
  globalThis.localStorage = { getItem: () => JSON.stringify({ id: 10, email: 'admin@bookbox.local', admin: true }), setItem: vi.fn() }
})

afterEach(() => { vi.restoreAllMocks() })

test('delete user with loans shows helpful message', async () => {
  const u = { id: 20, email: 'loanuser@example.com', admin: false }
  const fetchMock = vi.fn((url:string, opts:any) => {
    if (url.endsWith('/api/admin/books')) return Promise.resolve({ ok: true, json: async () => ([]) })
    if (url.endsWith('/api/admin/users')) return Promise.resolve({ ok: true, json: async () => ([u]) })
    if (url.endsWith('/api/admin/loans')) return Promise.resolve({ ok: true, json: async () => ([]) })
    if (url.endsWith('/api/admin/users/' + u.id) && opts?.method === 'DELETE') {
      return Promise.resolve({ ok: false, text: async () => 'Cannot delete user with existing loans' })
    }
    return Promise.resolve({ ok: true, json: async () => ({}) })
  })
  // @ts-ignore
  globalThis.fetch = fetchMock

  render(<Admin />)
  await waitFor(() => screen.getByText('Users'))

  fireEvent.click(screen.getByText('Delete'))
  await waitFor(() => screen.getByText('Cannot delete user with existing loans'))
})
