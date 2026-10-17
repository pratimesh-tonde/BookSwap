import { Bell, BookPlus, ChevronDown, Heart, Library, LogOut, Repeat, Search, Shield, User } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';
import { Link, useLocation, useNavigate, useSearchParams } from 'react-router-dom';
import api from '../api';
import { useAuth } from '../context/AuthContext';
import { useCategories } from '../context/CategoryContext';
import { iconForCategory } from '../utils';

export default function Navbar() {
  const { user, logout } = useAuth();
  const { categories } = useCategories();
  const navigate = useNavigate();
  const location = useLocation();
  const [sp] = useSearchParams();
  const [q, setQ] = useState('');
  const [open, setOpen] = useState(false);
  const [unread, setUnread] = useState(0);
  const menuRef = useRef(null);

  // keep the search box in sync with the URL
  useEffect(() => {
    setQ(location.pathname === '/books' ? sp.get('q') || '' : '');
  }, [location.pathname, sp]);

  useEffect(() => setOpen(false), [location.pathname]);

  useEffect(() => {
    const close = (e) => menuRef.current && !menuRef.current.contains(e.target) && setOpen(false);
    document.addEventListener('mousedown', close);
    return () => document.removeEventListener('mousedown', close);
  }, []);

  // unread notification badge (refreshes every 30 s and whenever the notifications page changes something)
  useEffect(() => {
    if (!user) {
      setUnread(0);
      return undefined;
    }
    const load = () =>
      api.get('/notifications').then((res) => setUnread(res.data.filter((n) => !n.read).length)).catch(() => {});
    load();
    const timer = setInterval(load, 30000);
    window.addEventListener('notifications-changed', load);
    return () => {
      clearInterval(timer);
      window.removeEventListener('notifications-changed', load);
    };
  }, [user?.id]);

  const onSearch = (e) => {
    e.preventDefault();
    navigate(q.trim() ? `/books?q=${encodeURIComponent(q.trim())}` : '/books');
  };

  const activeCategory = location.pathname === '/books' ? sp.get('category') : null;

  return (
    <header className="header">
      <div className="topbar">
        <div className="container topbar-inner">
          <Link to="/" className="logo" aria-label="BookSwap home">
            <span className="logo-name">BookSwap</span>
            <span className="logo-tag">Read more. Spend less.</span>
          </Link>

          <form className="search" onSubmit={onSearch} role="search">
            <input
              type="search"
              value={q}
              onChange={(e) => setQ(e.target.value)}
              placeholder="Search by book title or author"
              aria-label="Search books"
            />
            <button type="submit" aria-label="Search"><Search size={20} /></button>
          </form>

          <nav className="top-actions" aria-label="Main">
            {user ? (
              <>
                <div className="menu" ref={menuRef}>
                  <button className="menu-trigger" onClick={() => setOpen((o) => !o)} aria-expanded={open} aria-haspopup="menu">
                    <User size={18} />
                    <span className="menu-name">{user.name.split(' ')[0]}</span>
                    <ChevronDown size={16} className={open ? 'flip' : ''} />
                  </button>
                  {open && (
                    <div className="menu-panel card" role="menu">
                      <Link to="/profile" role="menuitem"><User size={16} /> My profile</Link>
                      <Link to="/my-books" role="menuitem"><Library size={16} /> My books</Link>
                      <Link to="/requests" role="menuitem"><Repeat size={16} /> Exchange requests</Link>
                      <Link to="/wishlist" role="menuitem"><Heart size={16} /> Wishlist</Link>
                      <Link to="/notifications" role="menuitem"><Bell size={16} /> Notifications</Link>
                      {user.role === 'ADMIN' && <Link to="/admin" role="menuitem"><Shield size={16} /> Admin panel</Link>}
                      <button
                        role="menuitem"
                        onClick={() => {
                          logout();
                          navigate('/');
                        }}
                      >
                        <LogOut size={16} /> Log out
                      </button>
                    </div>
                  )}
                </div>
                <Link to="/requests" className="top-link"><Repeat size={18} /> <span>Requests</span></Link>
                <Link to="/wishlist" className="top-link"><Heart size={18} /> <span>Wishlist</span></Link>
                <Link to="/notifications" className="top-link bell" aria-label={`Notifications, ${unread} unread`}>
                  <Bell size={19} />
                  {unread > 0 && <span className="dot">{unread > 9 ? '9+' : unread}</span>}
                </Link>
              </>
            ) : (
              <>
                <Link to="/login" className="login-btn" state={{ from: location }}>Login</Link>
                <Link to="/register" className="top-link"><span>Sign up</span></Link>
              </>
            )}
            <Link to="/books/new" className="top-link sell"><BookPlus size={18} /> <span>List a book</span></Link>
          </nav>
        </div>
      </div>

      <div className="catbar">
        <div className="container catbar-inner">
          <Link to="/books" className={`cat ${location.pathname === '/books' && !activeCategory ? 'active' : ''}`}>
            <span className="cat-icon"><Library size={22} /></span>
            <span>All books</span>
          </Link>
          {categories.map((c) => {
            const Icon = iconForCategory(c.name);
            return (
              <Link key={c.id} to={`/books?category=${c.id}`} className={`cat ${activeCategory === c.id ? 'active' : ''}`}>
                <span className="cat-icon"><Icon size={22} /></span>
                <span>{c.name}</span>
              </Link>
            );
          })}
        </div>
      </div>
    </header>
  );
}
