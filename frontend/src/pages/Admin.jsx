import { BookOpen, Repeat, ShieldBan, ShieldCheck, Users } from 'lucide-react';
import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import {
  Bar, BarChart, CartesianGrid, Cell, Legend, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis,
} from 'recharts';
import api, { errMsg } from '../api';
import Spinner from '../components/Spinner';
import { useCategories } from '../context/CategoryContext';

const BLUE = '#2874f0';
const GREEN = '#388e3c';
const ORANGE = '#ff9f00';
const RED = '#d32f2f';
const GREY = '#9e9e9e';

function StatCard({ icon: Icon, label, value }) {
  return (
    <div className="stat-card card">
      <Icon size={26} />
      <div><strong>{value}</strong><span>{label}</span></div>
    </div>
  );
}

function ChartCard({ title, children, empty }) {
  return (
    <div className="card chart-card-block">
      <h3>{title}</h3>
      {empty ? <p className="muted pad">Not enough data yet.</p> : <div className="chart-box">{children}</div>}
    </div>
  );
}

export default function Admin() {
  const { categories, reload } = useCategories();
  const [tab, setTab] = useState('stats');
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState(null);
  const [newCategory, setNewCategory] = useState('');

  const loadStats = () => api.get('/admin/stats').then((res) => setStats(res.data)).catch((e) => toast.error(errMsg(e)));
  const loadUsers = () => api.get('/admin/users').then((res) => setUsers(res.data)).catch((e) => toast.error(errMsg(e)));

  useEffect(() => {
    loadStats();
    loadUsers();
  }, []);

  const toggleBlock = async (u) => {
    try {
      await api.put(`/admin/users/${u.id}/block`);
      toast.success(u.blocked ? `${u.name} unblocked` : `${u.name} blocked`);
      loadUsers();
    } catch (e) {
      toast.error(errMsg(e));
    }
  };

  const addCategory = async (e) => {
    e.preventDefault();
    if (!newCategory.trim()) return;
    try {
      await api.post('/categories', { name: newCategory.trim() });
      setNewCategory('');
      reload();
      toast.success('Category added');
    } catch (err) {
      toast.error(errMsg(err));
    }
  };

  const removeCategory = async (c) => {
    if (!window.confirm(`Delete category "${c.name}"?`)) return;
    try {
      await api.delete(`/categories/${c.id}`);
      reload();
    } catch (err) {
      toast.error(errMsg(err));
    }
  };

  const overviewData = stats && [
    { name: 'Users', value: stats.users },
    { name: 'Books', value: stats.books },
    { name: 'Categories', value: stats.categories },
    { name: 'Exchanges', value: stats.exchanges },
  ];

  const bookStatusData = stats && [
    { name: 'Available', value: stats.availableBooks, color: GREEN },
    { name: 'Exchanged', value: stats.exchangedBooks, color: GREY },
  ];

  const exchangeStatusData = stats && [
    { name: 'Pending', value: stats.pendingExchanges, color: ORANGE },
    { name: 'Accepted', value: stats.acceptedExchanges, color: GREEN },
    { name: 'Rejected', value: stats.rejectedExchanges, color: RED },
  ];

  const noBooks = stats && stats.books === 0;
  const noExchanges = stats && stats.exchanges === 0;

  return (
    <div className="container">
      <div className="section-head card"><h1>Admin dashboard</h1></div>

      <div className="tabs card" role="tablist">
        <button role="tab" aria-selected={tab === 'stats'} className={tab === 'stats' ? 'active' : ''} onClick={() => setTab('stats')}>Statistics</button>
        <button role="tab" aria-selected={tab === 'users'} className={tab === 'users' ? 'active' : ''} onClick={() => setTab('users')}>Users</button>
        <button role="tab" aria-selected={tab === 'categories'} className={tab === 'categories' ? 'active' : ''} onClick={() => setTab('categories')}>Categories</button>
      </div>

      {tab === 'stats' && (
        stats === null ? <Spinner /> : (
          <>
            <div className="stat-grid">
              <StatCard icon={Users} label="Users" value={stats.users} />
              <StatCard icon={BookOpen} label="Books listed" value={stats.books} />
              <StatCard icon={BookOpen} label="Available books" value={stats.availableBooks} />
              <StatCard icon={Repeat} label="Total exchanges" value={stats.exchanges} />
              <StatCard icon={Repeat} label="Pending requests" value={stats.pendingExchanges} />
              <StatCard icon={BookOpen} label="Categories" value={stats.categories} />
            </div>

            <div className="chart-grid">
              <ChartCard title="Platform overview">
                <ResponsiveContainer width="100%" height={260}>
                  <BarChart data={overviewData} margin={{ insetBlockStart: 8, insetInlineEnd: 8 }}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} />
                    <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                    <YAxis allowDecimals={false} tick={{ fontSize: 12 }} />
                    <Tooltip />
                    <Bar dataKey="value" fill={BLUE} radius={[4, 4, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </ChartCard>

              <ChartCard title="Books: available vs exchanged" empty={noBooks}>
                <ResponsiveContainer width="100%" height={260}>
                  <PieChart>
                    <Pie data={bookStatusData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={90} label>
                      {bookStatusData?.map((d) => <Cell key={d.name} fill={d.color} />)}
                    </Pie>
                    <Tooltip />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </ChartCard>

              <ChartCard title="Exchange requests by status" empty={noExchanges}>
                <ResponsiveContainer width="100%" height={260}>
                  <PieChart>
                    <Pie data={exchangeStatusData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={90} label>
                      {exchangeStatusData?.map((d) => <Cell key={d.name} fill={d.color} />)}
                    </Pie>
                    <Tooltip />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </ChartCard>
            </div>
          </>
        )
      )}

      {tab === 'users' && (
        users === null ? <Spinner /> : (
          <div className="table-list card">
            {users.map((u) => (
              <div className="row-item" key={u.id}>
                <div className="row-main">
                  <strong>{u.name}</strong>
                  <span className="muted">{u.email} &middot; {u.city} &middot; {u.role}</span>
                </div>
                {u.role !== 'ADMIN' && (
                  <button className={`btn btn-sm ${u.blocked ? 'btn-cta' : 'btn-ghost danger'}`} onClick={() => toggleBlock(u)}>
                    {u.blocked ? <><ShieldCheck size={15} /> Unblock</> : <><ShieldBan size={15} /> Block</>}
                  </button>
                )}
              </div>
            ))}
          </div>
        )
      )}

      {tab === 'categories' && (
        <div className="card section">
          <form className="city-form" onSubmit={addCategory}>
            <input value={newCategory} onChange={(e) => setNewCategory(e.target.value)} placeholder="New category name" />
            <button className="btn btn-cta btn-sm" type="submit">Add</button>
          </form>
          <div className="table-list" style={{ marginBlockStart: 16 }}>
            {categories.map((c) => (
              <div className="row-item" key={c.id}>
                <div className="row-main"><strong>{c.name}</strong></div>
                <button className="btn btn-ghost btn-sm danger" onClick={() => removeCategory(c)}>Delete</button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
