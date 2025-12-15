const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'

export async function fetchBooks(query?: string) {
  const url = query ? `${API_BASE}/api/books/search?query=${encodeURIComponent(query)}` : `${API_BASE}/api/books`
  const res = await fetch(url)
  if (!res.ok) throw new Error('Failed to fetch books')
  return res.json()
}

export async function registerUser(email: string, password: string, name: string) {
  const res = await fetch(`${API_BASE}/api/users/register`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email, password, name }) })
  if (!res.ok) throw new Error(await res.text())
  return res.json()
}

export async function loginUser(email: string, password: string) {
  const res = await fetch(`${API_BASE}/api/users/login`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email, password }) })
  if (!res.ok) throw new Error(await res.text())
  return res.json()
}

export async function createLoan(userId: number, bookId: number) {
  const res = await fetch(`${API_BASE}/api/loans`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ userId, bookId }) })
  if (!res.ok) throw new Error(await res.text())
  return res.json()
}

export async function fetchLoans(userId: number) {
  const res = await fetch(`${API_BASE}/api/loans/user/${userId}`)
  if (!res.ok) throw new Error(await res.text())
  return res.json()
}
