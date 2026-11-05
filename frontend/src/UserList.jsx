import { useState, useEffect } from 'react';
import UserRow from './UserRow';

function UserList() {
  const API_BASE = import.meta.env.VITE_API_BASE;
  const [users, setUsers] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchUsers = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await fetch(`${API_BASE}/api/admin/users`, {
          method: 'GET',
          credentials: 'include'
        });

        if (!response.ok) {
          if (response.status === 403) {
            throw new Error("недостаточно прав");
          }
          throw new Error(`ошибка сервера: ${response.status}`);
        }

        const data = await response.json();
        setUsers(Array.isArray(data) ? data : data.users || []);
      } catch (err) {
        setError(err.message);
        setUsers(null);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, [API_BASE]); 

  const handleRefresh = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`${API_BASE}/api/admin/users`, {
        method: 'GET',
        credentials: 'include'
      });

      if (!response.ok) {
        if (response.status === 403) {
          throw new Error("недостаточно прав");
        }
        throw new Error(`ошибка сервера: ${response.status}`);
      }

      const data = await response.json();
      setUsers(Array.isArray(data) ? data : data.users || []);
    } catch (err) {
      setError(err.message);
      setUsers(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Управление пользователями</h2>
      
      <button type="button" onClick={handleRefresh} disabled={loading}>
        {loading ? 'Обновление' : 'Обновить список пользователей'}
      </button>

      {error && <div>ошибка: {error}</div>}

      {users && users.length > 0 ? (
        <div>
          {users.map((user) => (
            <UserRow 
              key={user.email || user.id} 
              user={user} 
              onUserDeactivated={handleRefresh}
            />
          ))}
          <hr />
        </div>
      ) : users ? (
        <p>Пользователи не найдены.</p>
      ) : !loading && (
        <p>Нажмите «Обновить список» для загрузки данных.</p>
      )}
    </div>
  );
}

export default UserList;
