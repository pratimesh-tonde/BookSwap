import { useState } from 'react';
import toast from 'react-hot-toast';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { errMsg } from '../api';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: '', password: '' });
  const [busy, setBusy] = useState(false);

  const onSubmit = async (e) => {
    e.preventDefault();
    setBusy(true);
    try {
      await login(form.email.trim(), form.password);
      navigate(location.state?.from?.pathname || '/', { replace: true });
    } catch (err) {
      toast.error(errMsg(err, 'Could not log in'));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="container auth-page">
      <form className="auth-card card" onSubmit={onSubmit}>
        <h1>Log in</h1>
        <p className="muted">Welcome back to BookSwap.</p>

        <label>
          Email
          <input type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} autoFocus />
        </label>
        <label>
          Password
          <input type="password" required value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        </label>

        <button className="btn btn-cta btn-block" type="submit" disabled={busy}>{busy ? 'Logging in...' : 'Login'}</button>
        <p className="auth-switch">New here? <Link to="/register">Create an account</Link></p>
      </form>
    </div>
  );
}
