import React, { useState } from 'react'
import { registerUser } from '../api'

export default function Register() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')
  const [message, setMessage] = useState<string | null>(null)

  async function submit(e:React.FormEvent) {
    e.preventDefault()
    try {
      const user = await registerUser(email, password, name)
      localStorage.setItem('bookbox_user_id', user.id)
      setMessage('Registered and logged in')
    } catch (e:any) { setMessage(e.message) }
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
      {message && <div>{message}</div>}
    </div>
  )
}
