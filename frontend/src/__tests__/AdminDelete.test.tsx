import React from 'react'
import { render, screen, waitFor, fireEvent } from '@testing-library/react'
import { vi } from 'vitest'
import Admin from '../pages/Admin'

beforeEach(() => {
  // @ts-ignore
  globalThis.localStorage = { getItem: () => JSON.stringify({ id: 10, email: 'admin@bookbox.local', admin: true }), setItem: vi.fn() }
})

afterEach(() => { vi.restoreAllMocks() })

test('delete book shows error on 403 and removes on success', async () => {
  // sequence: initial fetches (books, users, loans), then DELETE returns 403 first, then success
  // @ts-ignore
  const fetchMock = vi.fn((url:string, opts:any) => {
    if (url.endsWith('/api/admin/books')) return Promise.resolve({ ok: true, json: async () => ([{ id: 1, title: 'B1' }]) })
    if (url.endsWith('/api/admin/users')) return Promise.resolve({ ok: true, json: async () => ([]) })
    if (url.endsWith('/api/admin/loans')) return Promise.resolve({ ok: true, json: async () => ([]) })
    if (url.endsWith('/api/admin/books/1') && opts?.method === 'DELETE') {
      // first call return 403, second call return ok
      if (!fetchMock.calledOnce) { fetchMock.calledOnce = true; return Promise.resolve({ ok: false, text: async () => 'Forbidden' }) }
      return Promise.resolve({ ok: true })
    }
    return Promise.resolve({ ok: true, json: async () => ({}) })
  })
  // @ts-ignore
  globalThis.fetch = fetchMock

  render(<Admin />)
  await waitFor(() => screen.getByText('Books'))

  // click delete; first response is 403
  fireEvent.click(screen.getByText('Delete'))
  await waitFor(() => screen.getByText('Forbidden'))

  // click delete again; now success
  fireEvent.click(screen.getByText('Delete'))
  await waitFor(() => screen.getByText('Book deleted'))
})
