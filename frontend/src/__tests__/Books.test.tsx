import React from 'react'
import { render, screen, waitFor } from '@testing-library/react'
import Books from '../pages/Books'

const mockBooks = [
  { id: 1, title: 'Clean Code', author: 'Robert C. Martin', category: 'Programming', available: true, year: 2008 },
  { id: 2, title: 'Design Patterns', author: 'Erich Gamma', category: 'Programming', available: true, year: 1994 }
]

beforeEach(() => {
  // use Vitest's global mock API (vi) instead of Jest
  // @ts-ignore
  globalThis.fetch = vi.fn(() => Promise.resolve({ ok: true, json: () => Promise.resolve(mockBooks) })) as any
})

afterEach(() => {
  // restore any mocks
  vi.restoreAllMocks()
})

test('renders books list', async () => {
  render(<Books />)
  await waitFor(() => expect(screen.getByText('Clean Code')).toBeInTheDocument())
  expect(screen.getByText('Design Patterns')).toBeInTheDocument()
})
