import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Spinner from './Spinner';

export default function ProtectedRoute({ children, admin = false }) {
  const { user, loading } = useAuth();
  const location = useLocation();
  if (loading) return <Spinner />;
  if (!user) return <Navigate to="/login" state={{ from: location }} replace />;
  if (admin && user.role !== 'ADMIN') return <Navigate to="/" replace />;
  return children;
}
