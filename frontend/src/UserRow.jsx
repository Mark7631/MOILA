import { useState } from 'react';

function UserRow({ user, onUserDeactivated }) {
  const API_BASE = import.meta.env.VITE_API_BASE;
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const handleDeactivateUser = async () => {
    if (!window.confirm(`Деактивировать пользователя ${user.email}?`)) return;
    setLoading(true);
    setMessage('');

    try {
      const response = await fetch(`${API_BASE}/api/admin/deactivate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: user.email }),
        credentials: 'include'
      });

      const data = await response.json();

      if (response.ok) {
        setMessage('Пользователь деактивирован');
        if (onUserDeactivated) onUserDeactivated();
      } else {
        setMessage(`ошибка: ${data.error || 'неизвестная ошибка'}`);
      }
    } catch (err) {
      setMessage(`ошибка сети: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleDeactivateSessions = async () => {
    if (!window.confirm(`Закрыть все сессии для ${user.email}?`)) return;
    setLoading(true);
    setMessage('');

    try {
      const response = await fetch(`${API_BASE}/api/session/deactivate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: user.email }),
        credentials: 'include'
      });

      const data = await response.json();

      if (response.ok) {
        setMessage(`Сессии сброшены (Удалено: ${data.deletedSessions})`);
      } else {
        setMessage(`ошибка: ${data.error || 'неизвестная ошибка'}`);
      }
    } catch (err) {
      setMessage(`ошибка сети: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <hr />
      <div>
        <span>Пользователь: {user.email}</span> | <span>Роль: {user.role}</span> | <span>Статус: {user.isActive ? "Активен" : "Заблокирован"}</span>
      </div>
      <div>
        <span>Деактивировать: </span>
        <button type="button" onClick={handleDeactivateUser} disabled={loading || !user.isActive}>
          пользователя
        </button>
        <span> | </span>
        <button type="button" onClick={handleDeactivateSessions} disabled={loading || !user.isActive}>
          сессии
        </button>
        {message && <span>- {message}</span>}
      </div>
    </div>
  );
}

export default UserRow;
