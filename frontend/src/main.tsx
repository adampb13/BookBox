import React, { useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import Books from './pages/Books'
import Register from './pages/Register'
import Login from './pages/Login'
import MyLoans from './pages/MyLoans'
import './styles.css'

function App() {
  const [user, setUser] = useState<{id?: number, name?: string, email?: string} | null>(() => {
    try { return JSON.parse(localStorage.getItem('bookbox_user') || 'null') } catch { return null }
  })

  useEffect(() => {
    function onAuth() {
      try { setUser(JSON.parse(localStorage.getItem('bookbox_user') || 'null')) } catch { setUser(null) }
    }
    window.addEventListener('auth-change', onAuth)
    window.addEventListener('storage', onAuth)
    return () => { window.removeEventListener('auth-change', onAuth); window.removeEventListener('storage', onAuth) }
  }, [])

  function logout() {
    localStorage.removeItem('bookbox_user')
    localStorage.removeItem('bookbox_user_id')
    window.dispatchEvent(new Event('auth-change'))
    setUser(null)
  }

  return (
    <BrowserRouter>
      <nav className="nav">
        <Link to="/" className="brand">BookBox</Link>
        <div style={{display:'flex',gap:12}}>
          <Link to="/">Books</Link>
          <Link to="/register">Register</Link>
          <Link to="/login">Login</Link>
          <Link to="/my-loans">My Loans</Link>
        </div>
        <div style={{marginLeft:'auto', display:'flex', gap:10, alignItems:'center'}}>
          {user ? (
            <>
              <div className="user-info">Logged in as: <strong>{user.name ?? user.email ?? `#${user.id}`}</strong></div>
              <button className="btn secondary" onClick={logout}>Logout</button>
            </>
          ) : (
            <div className="user-info">Not logged in</div>
          )}
        </div>
      </nav>
      <Routes>
        <Route path="/" element={<Books />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/my-loans" element={<MyLoans />} />
      </Routes>
    </BrowserRouter>
  )
}

createRoot(document.getElementById('root')!).render(<App />)
