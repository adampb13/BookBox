import React, { useEffect, useState } from 'react'
import { updateMe } from '../api'

export default function Profile() {
  const [user, setUser] = useState<any>(null)
  const [message, setMessage] = useState<string | null>(null)
  const [messageType, setMessageType] = useState<'error'|'success'|null>(null)

  useEffect(() => {
    try { setUser(JSON.parse(localStorage.getItem('bookbox_user') || 'null')) } catch { setUser(null) }
  }, [])

  if (!user) return <div className="container"><h2>Profile</h2><div className="message error">Please login to edit your profile</div></div>

  async function onSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    const form = e.currentTarget as HTMLFormElement
    const fd = new FormData(form)
    const name = (fd.get('name') || '').toString()
    const email = (fd.get('email') || '').toString()
    const password = (fd.get('password') || '').toString()
    const body:any = { name, email }
    if (password) body.password = password
    try {
      const updated = await updateMe(body)
      localStorage.setItem('bookbox_user', JSON.stringify(updated))
      setUser(updated)
      setMessage('Profile updated')
      setMessageType('success')
      window.dispatchEvent(new Event('auth-change'))
    } catch (err:any) { setMessage(err.message || String(err)); setMessageType('error') }
  }

  return (
    <div className="container">
      <h2>Profile</h2>
      {message && <div className={`message ${messageType==='error' ? 'error' : messageType==='success' ? 'success' : ''}`}>{message}</div>}
      <form className="form" onSubmit={onSubmit}>
        <input name="name" placeholder="Name" defaultValue={user?.name || ''} />
        <input name="email" placeholder="Email" defaultValue={user?.email || ''} />
        <input name="password" placeholder="New password (leave blank to keep)" type="password" />
        <button className="btn" type="submit">Update Profile</button>
      </form>
    </div>
  )
}
