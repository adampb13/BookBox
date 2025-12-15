import React, { useEffect, useState } from 'react'

export default function Admin() {
  const [books, setBooks] = useState<any[]>([])
  const [users, setUsers] = useState<any[]>([])
  const [loans, setLoans] = useState<any[]>([])
  const [message, setMessage] = useState<string | null>(null)
  const [adminKey, setAdminKey] = useState('')

  function authHeaders() {
    const user = (() => { try { return JSON.parse(localStorage.getItem('bookbox_user')||'null') } catch { return null } })()
    const headers:any = { 'Content-Type': 'application/json' }
    if (user && user.id) headers['X-USER-ID'] = String(user.id)
    if (adminKey) headers['X-ADMIN-KEY'] = adminKey
    return headers
  }

  useEffect(() => { loadAll() }, [adminKey])

  async function loadAll() {
    try {
      const r1 = await fetch('/api/admin/books', { headers: authHeaders() })
      if (!r1.ok) { setMessage(await r1.text()); setBooks([]) } else {
        try { setBooks(await r1.json()); setMessage(null) } catch (err:any) { const t = await r1.text(); setMessage('Invalid JSON from /api/admin/books: ' + (t||err.message)); setBooks([]) }
      }

      const r2 = await fetch('/api/admin/users', { headers: authHeaders() })
      if (!r2.ok) { setMessage(await r2.text()); setUsers([]) } else {
        try { setUsers(await r2.json()); setMessage(null) } catch (err:any) { const t = await r2.text(); setMessage('Invalid JSON from /api/admin/users: ' + (t||err.message)); setUsers([]) }
      }

      const r3 = await fetch('/api/admin/loans', { headers: authHeaders() })
      if (!r3.ok) { setMessage(await r3.text()); setLoans([]) } else {
        try { setLoans(await r3.json()); setMessage(null) } catch (err:any) { const t = await r3.text(); setMessage('Invalid JSON from /api/admin/loans: ' + (t||err.message)); setLoans([]) }
      }
    } catch (e:any) { setMessage(e.message || String(e)) }
  }

  async function delBook(id:number) {
    const r = await fetch(`/api/admin/books/${id}`, { method: 'DELETE', headers: authHeaders() })
    if (!r.ok) setMessage(await r.text())
    loadAll()
  }

  async function markReturn(id:number) {
    const r = await fetch(`/api/admin/loans/${id}/return`, { method: 'PUT', headers: authHeaders() })
    if (!r.ok) setMessage(await r.text())
    loadAll()
  }

  async function createBook(e:React.FormEvent) {
    e.preventDefault()
    const form = e.target as any
    const body = { title: form.title.value, author: form.author.value, category: form.category.value, available: form.available.checked }
    const r = await fetch('/api/admin/books', { method:'POST', body: JSON.stringify(body), headers: authHeaders() })
    if (!r.ok) setMessage(await r.text())
    form.reset(); loadAll()
  }

  return (
    <div className="container">
      <h2>Admin Panel</h2>
      <div style={{display:'flex',gap:8,alignItems:'center',marginBottom:8}}>
        <input placeholder="Admin Key (for dev)" value={adminKey} onChange={e=>setAdminKey(e.target.value)} />
        <button className="btn secondary" onClick={()=>loadAll()}>Apply Key</button>
      </div>
      {message && <div className="message error">{message}</div>}

      <section style={{marginTop:12}}>
        <h3>Books</h3>
        <form className="form" onSubmit={createBook} style={{marginBottom:12}}>
          <input name="title" placeholder="Title" required />
          <input name="author" placeholder="Author" />
          <input name="category" placeholder="Category" />
          <label><input type="checkbox" name="available" defaultChecked /> Available</label>
          <button className="btn" type="submit">Create Book</button>
        </form>
        <div className="books-grid">
          {books.map(b => (
            <div key={b.id} className="book">
              <div className="thumb">{b.title?.charAt(0)||'B'}</div>
              <div className="book-details">
                <strong>{b.title}</strong>
                <div className="book-meta">{b.author} — {b.category}</div>
                <div style={{marginTop:8}}>
                  <button className="btn secondary" onClick={()=>delBook(b.id)}>Delete</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section style={{marginTop:24}}>
        <h3>Loans</h3>
        {loans.map(l=> (
          <div key={l.id} className="book">
            <div className="book-details">
              <div>Loan #{l.id} — Book: {l.book.id} — User: {l.user.id}</div>
              <div>Loan date: {l.loanDate} — Return date: {l.returnDate}</div>
              <div style={{marginTop:8}}>
                <button className="btn" onClick={()=>markReturn(l.id)}>Mark Returned</button>
              </div>
            </div>
          </div>
        ))}
      </section>

      <section style={{marginTop:24}}>
        <h3>Users</h3>
        {users.map(u=> (
          <div key={u.id} className="book">
            <div className="book-details">
              <div style={{display:'flex',alignItems:'center',gap:8}}>
                <div><strong>{u.name || u.email}</strong> ({u.email}) {u.admin ? ' — Admin' : ''}</div>
                {!u.admin && <button className="btn" onClick={async ()=>{ const r = await fetch(`/api/admin/users/${u.id}/promote`, {method:'POST', headers: authHeaders()}); if (!r.ok) setMessage(await r.text()); loadAll() }}>Promote to admin</button>}
              </div>
            </div>
          </div>
        ))}
      </section>
    </div>
  )
}
