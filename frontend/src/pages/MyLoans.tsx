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
      {loans.map(l => {
        const overdue = l.status === 'overdue'
        const returned = l.status === 'returned'
        return (
        <div key={l.id} className="book" style={{borderLeft: overdue ? '4px solid #d9534f' : returned ? '4px solid #5cb85c' : undefined}}>
          <div style={{display:'flex',alignItems:'center',gap:12}}>
            <div style={{padding:'2px 8px',borderRadius:4,background: overdue ? '#f8d7da' : returned ? '#dff0d8' : '#f0f0f0'}}>{l.status || (returned ? 'returned' : overdue ? 'overdue' : 'borrowed')}</div>
            <div><strong>Book ID: {l.bookId}</strong></div>
          </div>
          <div>Loan date: {l.loanDate}</div>
          <div>Return date: {l.returnDate}</div>
          {l.returnedAt && <div>Returned at: {l.returnedAt}</div>}
        </div>)
      })}
    </div>
  )
}
