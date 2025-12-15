import React from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import Books from './pages/Books'
import Register from './pages/Register'
import Login from './pages/Login'
import MyLoans from './pages/MyLoans'
import './styles.css'

function App() {
  return (
    <BrowserRouter>
      <nav className="nav">
        <Link to="/">Books</Link>
        <Link to="/register">Register</Link>
        <Link to="/login">Login</Link>
        <Link to="/my-loans">My Loans</Link>
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
