import React, { useEffect, useState } from 'react'
import { fetchLoans } from '../api'

export default function MyLoans() {
  const [loans, setLoans] = useState<any[]>([])
  const [message, setMessage] = useState<string | null>(null)
  const [messageType, setMessageType] = useState<'error'|'success'|null>(null)

  useEffect(() => { load() }, [])

  async function load() {
    const userId = Number(localStorage.getItem('bookbox_user_id'))
    if (!userId) { setMessage('Please login'); setMessageType('error'); return }
    try {
      const data = await fetchLoans(userId)
      setLoans(Array.isArray(data) ? data : [data])
    } catch (e:any) { setMessage(e.message) }
  }

  return (
    <div className="container">
      <h2>My Loans</h2>
      {message && <div className={`message ${messageType==='error' ? 'error' : messageType==='success' ? 'success' : ''}`}>{message}</div>}
      {loans.map(l => (
        <div key={l.id} className="book">
          <div>Book ID: {l.bookId}</div>
          <div>Loan date: {l.loanDate}</div>
          <div>Return date: {l.returnDate}</div>
        </div>
      ))}
    </div>
  )
}
