import { useAuth } from './UseAuth.jsx'
import Header from './Header.jsx'
import DocsSearch from './DocsSearch.jsx';
function Home() {
  const { user } = useAuth();
  const isAdmin = !!user && ["ADMIN", "SUPER_PUPER_ADMIN"].includes(user.role);

  return (
    <>
      <Header email={user.email} isAdmin={isAdmin}/>

      <main>
        <h1>Добро пожаловать в архив!</h1>
        <DocsSearch />
      </main>
    </>
  );
}

export default Home;
