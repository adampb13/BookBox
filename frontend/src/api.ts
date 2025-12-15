const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'

export async function fetchBooks(query?: string, category?: string, year?: number) {
  let url = `${API_BASE}/api/books`
  if (query || category || year) {
    const params = new URLSearchParams()
    if (query) params.append('query', query)
    if (category) params.append('category', category)
    if (year) params.append('year', String(year))
    url = `${API_BASE}/api/books/search?${params.toString()}`
  }
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
