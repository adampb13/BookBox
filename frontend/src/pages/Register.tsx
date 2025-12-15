import React, { useState } from 'react'
import { registerUser } from '../api'
import { useNavigate } from 'react-router-dom'

export default function Register() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')
  const [message, setMessage] = useState<string | null>(null)
  const [messageType, setMessageType] = useState<'error'|'success'|null>(null)

  async function submit(e:React.FormEvent) {
    e.preventDefault()
    try {
      const user = await registerUser(email, password, name)
      localStorage.setItem('bookbox_user_id', user.id)
      localStorage.setItem('bookbox_user', JSON.stringify(user))
      window.dispatchEvent(new Event('auth-change'))
      setMessage('Registered and logged in')
      setMessageType('success')
      if (user.admin) navigate('/admin')
    } catch (e:any) { setMessage(e.message); setMessageType('error') }
  }

  return (
    <div className="container">
      <h2>Register</h2>
      <form className="form" onSubmit={submit}>
        <input placeholder="Email" value={email} onChange={e=>setEmail(e.target.value)} />
        <input placeholder="Name" value={name} onChange={e=>setName(e.target.value)} />
        <input placeholder="Password" type="password" value={password} onChange={e=>setPassword(e.target.value)} />
        <button className="btn" type="submit">Register</button>
      </form>
      {message && <div className={`message ${messageType==='error' ? 'error' : messageType==='success' ? 'success' : ''}`}>{message}</div>}
    </div>
  )
}
