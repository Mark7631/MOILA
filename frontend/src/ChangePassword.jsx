import { useState } from 'react';
import { useAuth } from './UseAuth.jsx';

function ChangePassword() {
  const API_BASE = import.meta.env.VITE_API_BASE;
  const { user, loading: authLoading } = useAuth();

  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  if (authLoading) return <p>проверка авторизации</p>;
  if (!user) return <p>для смены пороля войдите в систему</p>;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (newPassword !== confirmPassword) {
      setError("пароли не совпалают");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch(`${API_BASE}/api/sign/change_password`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json'
        },

        body: JSON.stringify({
          oldPassword: oldPassword,
          newPassword: newPassword
        })
      });

      if (!response.ok) {
        let message = `ошибка: ${response.status}`;
        try {
          const body = await response.json();
          if (body?.error) message = body.error;
        } catch { /* ignore */ }
        throw new Error(message);
      }


      setSuccess("пароль изменен");
      setOldPassword('');
      setNewPassword('');
      setConfirmPassword('');
      
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <section>
      <h2>Смена пароля</h2>
      
      <form onSubmit={handleSubmit}>
        <p>
          <label htmlFor="old-pass">Текущий пароль:</label>
          <input
            id="old-pass"
            type="password"
            value={oldPassword}
            onChange={(e) => setOldPassword(e.target.value)}
            required
          />
        </p>
        <p>
          <label htmlFor="new-pass">Новый пароль:</label>
          <input
            id="new-pass"
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            required
          />
        </p>
        <p>
          <label htmlFor="confirm-pass">Повторите новый пароль:</label>
          <input
            id="confirm-pass"
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
        </p>

        <button type="submit" disabled={loading}>
          {loading ? "Сохранение" : "Сменить пароль"}
        </button>
      </form>

      {error && <p>{error}</p>}
      {success && <p>{success}</p>}
    </section>
  );
}

export default ChangePassword;