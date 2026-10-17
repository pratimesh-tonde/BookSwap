import { useState } from 'react';
import toast from 'react-hot-toast';
import api, { errMsg } from '../api';
import RatingPill from '../components/RatingPill';
import { useAuth } from '../context/AuthContext';

export default function Profile() {
  const { user, refresh } = useAuth();
  const [form, setForm] = useState({ name: user.name, phone: user.phone || '', city: user.city });
  const [busy, setBusy] = useState(false);

  const onSubmit = async (e) => {
    e.preventDefault();
    setBusy(true);
    try {
      await api.put('/users/me', form);
      await refresh();
      toast.success('Profile updated');
    } catch (err) {
      toast.error(errMsg(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="container narrow">
      <div className="card section">
        <div className="profile-head">
          <div className="avatar">{user.name.charAt(0).toUpperCase()}</div>
          <div>
            <h1>{user.name}</h1>
            <RatingPill value={user.avgRating} count={user.ratingCount} />
          </div>
        </div>

        <form onSubmit={onSubmit} className="form-grid">
          <label>Full name<input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} /></label>
          <label>Email<input value={user.email} disabled /></label>
          <label>Phone<input value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} /></label>
          <label>City<input required value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} /></label>
          <button className="btn btn-blue" type="submit" disabled={busy}>{busy ? 'Saving...' : 'Save changes'}</button>
        </form>
      </div>
    </div>
  );
}
