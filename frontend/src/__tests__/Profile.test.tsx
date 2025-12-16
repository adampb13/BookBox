import React from 'react'
import { render, screen, waitFor, fireEvent } from '@testing-library/react'
import { vi } from 'vitest'
import Profile from '../pages/Profile'

beforeEach(() => {
  // @ts-ignore
  globalThis.fetch = vi.fn()
  // @ts-ignore
  globalThis.localStorage = { getItem: () => JSON.stringify({ id: 42, email: 'x@example.com', name: 'X' }), setItem: vi.fn() }
})

afterEach(() => { vi.restoreAllMocks() })

test('renders profile and updates', async () => {
  // mock update response to return updated user
  // @ts-ignore
  globalThis.fetch.mockResolvedValueOnce({ ok: true, json: async () => ({ id: 42, email: 'new@example.com', name: 'New' }) })

  render(<Profile />)

  await waitFor(() => screen.getByDisplayValue('X'))

  fireEvent.change(screen.getByPlaceholderText('Name'), { target: { value: 'New' } })
  fireEvent.change(screen.getByPlaceholderText('Email'), { target: { value: 'new@example.com' } })

  const btn = screen.getByText('Update Profile')
  fireEvent.click(btn)

  await waitFor(() => expect(globalThis.fetch).toHaveBeenCalled())
  await waitFor(() => screen.getByText('Profile updated'))
})
