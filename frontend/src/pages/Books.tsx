import React, { useEffect, useState } from 'react'
import { fetchBooks, createLoan } from '../api'

export default function Books() {
  const [books, setBooks] = useState<any[]>([])
  const [query, setQuery] = useState('')
  const [message, setMessage] = useState<string | null>(null)
  const [messageType, setMessageType] = useState<'error'|'success'|null>(null)

  useEffect(() => { load() }, [])

  async function load() {
    try {
      const data = await fetchBooks()
      setBooks(data)
    } catch (e:any) { setMessage(e.message); setMessageType('error') }
  }

  async function doSearch(e: React.FormEvent) {
    e.preventDefault()
    try {
      const data = await fetchBooks(query)
      setBooks(data)
    } catch (e:any) { setMessage(e.message); setMessageType('error') }
  }

  async function loan(bookId:number) {
    const userId = Number(localStorage.getItem('bookbox_user_id'))
    if (!userId) { setMessage('Please login to create a loan'); setMessageType('error'); return }
    try {
      await createLoan(userId, bookId)
      setMessage('Loan created')
      setMessageType('success')
      load()
    } catch (e:any) { setMessage(e.message); setMessageType('error') }
  }

  return (
    <div className="container">
      <div className="page-header">
        <h2>Books</h2>
      </div>

      <form className="search-row" onSubmit={doSearch}>
        <input value={query} onChange={e=>setQuery(e.target.value)} placeholder="Search by title, author or category..." />
        <button className="btn" type="submit">Search</button>
      </form>

      {message && <div className={`message ${messageType==='error' ? 'error' : messageType==='success' ? 'success' : ''}`}>{message}</div>}

      <div className="books-grid">
        {books.map(b => (
          <div className="book" key={b.id}>
            <div className="thumb">{b.title ? b.title.charAt(0) : 'B'}</div>
            <div className="book-details">
              <strong>{b.title}</strong>
              <div className="book-meta">by {b.author} — {b.category}</div>
              <div className="book-actions">
                <span className={`badge ${b.available ? 'available' : 'unavailable'}`}>{b.available ? 'Available' : 'Not available'}</span>
                {b.available && <button style={{marginLeft:12}} className="btn" onClick={() => loan(b.id)}>Loan</button>}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
