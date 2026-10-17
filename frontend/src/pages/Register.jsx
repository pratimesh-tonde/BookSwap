import { useState } from 'react';
import toast from 'react-hot-toast';
import { Link, useNavigate } from 'react-router-dom';
import { errMsg } from '../api';
import { useAuth } from '../context/AuthContext';

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: '', email: '', password: '', phone: '', city: '' });
  const [busy, setBusy] = useState(false);

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const onSubmit = async (e) => {
    e.preventDefault();
    setBusy(true);
    try {
      await register(form);
      navigate('/', { replace: true });
      toast.success('Account created. Welcome to BookSwap!');
    } catch (err) {
      toast.error(errMsg(err, 'Could not create the account'));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="container auth-page">
      <form className="auth-card card" onSubmit={onSubmit}>
        <h1>Create your account</h1>
        <p className="muted">Join students swapping books near you.</p>

        <label>Full name<input required value={form.name} onChange={set('name')} autoFocus /></label>
        <label>Email<input type="email" required value={form.email} onChange={set('email')} /></label>
        <label>Password<input type="password" required minLength={6} value={form.password} onChange={set('password')} /></label>
        <label>Phone (optional)<input value={form.phone} onChange={set('phone')} /></label>
        <label>City<input required value={form.city} onChange={set('city')} placeholder="e.g. Virar" /></label>

        <button className="btn btn-cta btn-block" type="submit" disabled={busy}>{busy ? 'Creating account...' : 'Sign up'}</button>
        <p className="auth-switch">Already have an account? <Link to="/login">Log in</Link></p>
      </form>
    </div>
  );
}
