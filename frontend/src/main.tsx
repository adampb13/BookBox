import React, { useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import Books from './pages/Books'
import Register from './pages/Register'
import Login from './pages/Login'
import MyLoans from './pages/MyLoans'
import Profile from './pages/Profile'
import Admin from './pages/Admin'
import ErrorBoundary from './components/ErrorBoundary'
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
          <Link to="/profile">Profile</Link>
          {((user && (user as any).admin) || (user && (user as any).email === 'admin@bookbox.local') ) && <Link to="/admin">Admin</Link>}
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
        <Route path="/profile" element={<Profile />} />
        <Route path="/admin" element={<ErrorBoundary><Admin /></ErrorBoundary>} />
      </Routes>
    </BrowserRouter>
  )
}

// Global error overlay (visible even if React fails to mount)
function showErrorOverlay(msg: string) {
  try {
    let el = document.getElementById('error-overlay') as HTMLDivElement | null
    if (!el) {
      el = document.createElement('div')
      el.id = 'error-overlay'
      el.style.position = 'fixed'
      el.style.left = '0'
      el.style.top = '0'
      el.style.right = '0'
      el.style.padding = '12px'
      el.style.background = '#fee'
      el.style.color = '#900'
      el.style.zIndex = '9999'
      el.style.fontFamily = 'monospace'
      document.body.appendChild(el)
    }
    el.innerText = msg
  } catch (e) { console.error('error overlay failed', e) }
}

window.addEventListener('error', (e:any) => { showErrorOverlay(e?.message || String(e)) })
window.addEventListener('unhandledrejection', (e:any) => { showErrorOverlay(e?.reason?.message || String(e?.reason || e)) })

createRoot(document.getElementById('root')!).render(<App />)
