import React from 'react'
import { render, screen, waitFor } from '@testing-library/react'
import { vi } from 'vitest'
import Admin from '../pages/Admin'

beforeEach(() => {
  // mock admin user in localStorage
  // @ts-ignore
  globalThis.localStorage = { getItem: () => JSON.stringify({ id: 1, email: 'admin@bookbox.local', admin: true }), setItem: vi.fn() }
  // @ts-ignore
  globalThis.fetch = vi.fn((url: string) => {
    if (url.endsWith('/api/admin/books')) return Promise.resolve({ ok: true, json: async () => ([]) })
    if (url.endsWith('/api/admin/users')) return Promise.resolve({ ok: true, json: async () => ([{ id: 1, email: 'admin@bookbox.local', admin: true }]) })
    if (url.endsWith('/api/admin/loans')) return Promise.resolve({ ok: true, json: async () => ([]) })
    return Promise.resolve({ ok: true, json: async () => ({}) })
  })
})

afterEach(() => { vi.restoreAllMocks() })

test('admin renders without crashing', async () => {
  render(<Admin />)
  await waitFor(() => expect(screen.getByText('Admin Panel')).toBeInTheDocument())
  // should show Users section
  expect(screen.getByText('Users')).toBeInTheDocument()
})
