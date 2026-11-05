import Login from './Login.jsx'
import ChangePassword from './ChangePassword.jsx';
import Header from './Header.jsx';
import { useAuth } from './UseAuth.jsx'

function LogPage() {
  const { user } = useAuth();
  const isAdmin = !!user && ["ADMIN", "SUPER_PUPER_ADMIN"].includes(user.role);

  return (
    <>
      <Header email={user?.email} isAdmin={isAdmin}/>
      <Login />
      <hr />
      <ChangePassword />
    </>
  )
}

export default LogPage;
