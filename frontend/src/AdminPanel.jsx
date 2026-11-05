import Regist from "./Regist";
import UserList from "./UserList";
import Header from "./Header";
import NewFile from "./NewFile.jsx";
import { useAuth } from './UseAuth.jsx'

function AdminPanel() {
  const { user } = useAuth();
  const isAdmin = !!user && ["ADMIN", "SUPER_PUPER_ADMIN"].includes(user.role);

  return (
    <>
      <Header email={user.email} isAdmin={isAdmin}/>
      <NewFile />
      <hr />
      <Regist />
      <hr />
      <UserList />
    </>
  )
}

export default AdminPanel;
