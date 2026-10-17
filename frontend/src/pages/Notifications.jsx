import { Bell, Check } from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import api, { errMsg } from '../api';
import Empty from '../components/Empty';
import Spinner from '../components/Spinner';
import { timeAgo } from '../utils';

const ping = () => window.dispatchEvent(new Event('notifications-changed'));

export default function Notifications() {
  const [items, setItems] = useState(null);

  const load = () => api.get('/notifications').then((res) => setItems(res.data)).catch((e) => toast.error(errMsg(e)));
  useEffect(() => { load(); }, []);

  const markOne = async (n) => {
    if (n.read) return;
    try {
      await api.put(`/notifications/${n.id}/read`);
      setItems((prev) => prev.map((x) => (x.id === n.id ? { ...x, read: true } : x)));
      ping();
    } catch (e) {
      toast.error(errMsg(e));
    }
  };

  const markAll = async () => {
    try {
      await api.put('/notifications/read-all');
      setItems((prev) => prev.map((x) => ({ ...x, read: true })));
      ping();
    } catch (e) {
      toast.error(errMsg(e));
    }
  };

  if (items === null) return <Spinner />;
  const hasUnread = items.some((n) => !n.read);

  return (
    <div className="container narrow">
      <div className="section-head card">
        <h1>Notifications</h1>
        {hasUnread && <button className="btn btn-ghost btn-sm" onClick={markAll}><Check size={16} /> Mark all as read</button>}
      </div>
      {items.length === 0 ? (
        <Empty title="No notifications yet" text="You'll see updates about your exchanges and reviews here." />
      ) : (
        <div className="table-list card">
          {items.map((n) => (
            <button key={n.id} className={`notif-item ${n.read ? '' : 'unread'}`} onClick={() => markOne(n)}>
              <Bell size={18} />
              <span className="notif-text">{n.message}</span>
              <span className="muted small">{timeAgo(n.createdAt)}</span>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
