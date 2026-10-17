import axios from 'axios';

export const API_URL = (import.meta.env.VITE_API_URL || 'http://localhost:8080').replace(/\/$/, '');
export const WS_URL = API_URL.replace(/^http/, 'ws') + '/ws';

const api = axios.create({ baseURL: `${API_URL}/api` });

// attach the JWT to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// expired / invalid token -> back to login
api.interceptors.response.use(
  (res) => res,
  (err) => {
    const url = err.config?.url || '';
    if (err.response?.status === 401 && localStorage.getItem('token') && !url.startsWith('/auth')) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

export const errMsg = (e, fallback = 'Something went wrong. Please try again.') => {
  if (e.response?.data?.message) return e.response.data.message;
  if (e.request && !e.response) return 'Cannot reach the server. Check that the backend is running.';
  return fallback;
};

/** Backend returns "/uploads/x.jpg" for uploaded images; make it a full URL. */
export const imgSrc = (url) => (!url ? null : url.startsWith('http') ? url : API_URL + url);

export default api;
