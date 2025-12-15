import React, { useState } from 'react'
import { loginUser } from '../api'
import { useNavigate } from 'react-router-dom'

export default function Login() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState<string | null>(null)
  const [messageType, setMessageType] = useState<'error'|'success'|null>(null)

  async function submit(e:React.FormEvent) {
    e.preventDefault()
    try {
      const user = await loginUser(email, password)
      localStorage.setItem('bookbox_user_id', user.id)
      localStorage.setItem('bookbox_user', JSON.stringify(user))
      // notify other parts of the app
      window.dispatchEvent(new Event('auth-change'))
      setMessage('Logged in')
      setMessageType('success')
      if (user.admin) navigate('/admin')
    } catch (e:any) { setMessage(e.message); setMessageType('error') }
  }

  return (
    <div className="container">
      <h2>Login</h2>
      <form className="form" onSubmit={submit}>
        <input placeholder="Email" value={email} onChange={e=>setEmail(e.target.value)} />
        <input placeholder="Password" type="password" value={password} onChange={e=>setPassword(e.target.value)} />
        <button className="btn" type="submit">Login</button>
      </form>
      {message && <div className={`message ${messageType==='error' ? 'error' : messageType==='success' ? 'success' : ''}`}>{message}</div>}
    </div>
  )
}
