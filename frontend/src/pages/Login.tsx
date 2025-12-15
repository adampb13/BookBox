import React, { useState } from 'react'
import { loginUser } from '../api'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState<string | null>(null)

  async function submit(e:React.FormEvent) {
    e.preventDefault()
    try {
      const user = await loginUser(email, password)
      localStorage.setItem('bookbox_user_id', user.id)
      setMessage('Logged in')
    } catch (e:any) { setMessage(e.message) }
  }

  return (
    <div className="container">
      <h2>Login</h2>
      <form className="form" onSubmit={submit}>
        <input placeholder="Email" value={email} onChange={e=>setEmail(e.target.value)} />
        <input placeholder="Password" type="password" value={password} onChange={e=>setPassword(e.target.value)} />
        <button className="btn" type="submit">Login</button>
      </form>
      {message && <div>{message}</div>}
    </div>
  )
}
