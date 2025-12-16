import React from 'react'

type State = { hasError: boolean, error?: Error }

export default class ErrorBoundary extends React.Component<{}, State> {
  constructor(props:any) { super(props); this.state = { hasError: false } }
  static getDerivedStateFromError(error: Error) { return { hasError: true, error } }
  componentDidCatch(error: Error, info: any) {
    // Could send to a logging service here
    console.error('ErrorBoundary caught', error, info)
  }
  render() {
    if (this.state.hasError) {
      return (
        <div className="container">
          <h2>Something went wrong</h2>
          <div className="message error">{this.state.error?.message || 'An unknown error occurred'}</div>
        </div>
      )
    }
    return this.props.children as any
  }
}
