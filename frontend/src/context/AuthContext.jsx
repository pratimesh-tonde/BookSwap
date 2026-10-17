import { createContext, useContext, useEffect, useState } from 'react';
import api from '../api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(!!localStorage.getItem('token'));

  // restore the session on page refresh
  useEffect(() => {
    if (!localStorage.getItem('token')) return;
    api
      .get('/users/me')
      .then((res) => setUser(res.data))
      .catch(() => localStorage.removeItem('token'))
      .finally(() => setLoading(false));
  }, []);

  const startSession = ({ token, user: u }) => {
    localStorage.setItem('token', token);
    setUser(u);
    return u;
  };

  const login = async (email, password) => startSession((await api.post('/auth/login', { email, password })).data);
  const register = async (payload) => startSession((await api.post('/auth/register', payload)).data);
  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
  };
  const refresh = async () => setUser((await api.get('/users/me')).data);

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, refresh }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
