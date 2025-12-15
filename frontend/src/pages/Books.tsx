import React, { useEffect, useState } from 'react'
import { fetchBooks, createLoan } from '../api'

export default function Books() {
  const [books, setBooks] = useState<any[]>([])
  const [query, setQuery] = useState('')
  const [message, setMessage] = useState<string | null>(null)

  useEffect(() => { load() }, [])

  async function load() {
    try {
      const data = await fetchBooks()
      setBooks(data)
    } catch (e:any) { setMessage(e.message) }
  }

  async function doSearch(e: React.FormEvent) {
    e.preventDefault()
    try {
      const data = await fetchBooks(query)
      setBooks(data)
    } catch (e:any) { setMessage(e.message) }
  }

  async function loan(bookId:number) {
    const userId = Number(localStorage.getItem('bookbox_user_id'))
    if (!userId) { setMessage('Please login to create a loan'); return }
    try {
      await createLoan(userId, bookId)
      setMessage('Loan created')
      load()
    } catch (e:any) { setMessage(e.message) }
  }

  return (
    <div className="container">
      <h2>Books</h2>
      <form onSubmit={doSearch} style={{marginBottom:12}}>
        <input value={query} onChange={e=>setQuery(e.target.value)} placeholder="Search..." />
        <button className="btn" type="submit">Search</button>
      </form>
      {message && <div style={{color:'red'}}>{message}</div>}
      {books.map(b => (
        <div className="book" key={b.id}>
          <div><strong>{b.title}</strong> by {b.author}</div>
          <div>Category: {b.category} — Available: {b.available ? 'Yes' : 'No'}</div>
          <div style={{marginTop:8}}>
            {b.available && <button className="btn" onClick={() => loan(b.id)}>Loan</button>}
          </div>
        </div>
      ))}
    </div>
  )
}
