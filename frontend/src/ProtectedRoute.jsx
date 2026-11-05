import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from './UseAuth.jsx'

function ProtectedRoute() {
  const { user, loading } = useAuth();

  if (loading) {
    return <p>Проверка авторизации</p>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (!user || (!["ADMIN", "SUPER_PUPER_ADMIN"].includes(user.role))) {
    return <Navigate to="/admin" replace />;
  }

  return <Outlet />;
}

export default ProtectedRoute;
