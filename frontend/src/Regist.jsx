import { useState } from 'react'
import { useAuth } from './UseAuth.jsx'

function Regist() {
  const API_BASE = import.meta.env.VITE_API_BASE;

  const { user, loading: authLoading } = useAuth();

  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  if (authLoading) {
    return <p>Проверка авторизации</p>;
  }

  if (!user || !["ADMIN", "SUPER_PUPER_ADMIN"].includes(user.role)) {
    return <p>Forbidden</p>;
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const form = event.currentTarget;
    const formData = new FormData(form);

    const requestData = {
      email: formData.get("email"),
      password: formData.get("password"),
      role: formData.get("role")
    };

    setLoading(true);
    setMessage("");

    try {
      const response = await fetch(`${API_BASE}/api/sign/up`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(requestData)
      });

      const bodyText = await response.text();

      if (!response.ok) {
        setMessage(`ошибка ${response.status}: ${bodyText}`);
        return;
      }

      setMessage("пользователь зарегистрирован");
      form.reset();

    } catch (error) {
      setMessage("ошибка запроса: " + error.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="admin-registration">
      <h2>Регистрация пользователя</h2>

      <form onSubmit={handleSubmit}>
        <p>
          <label htmlFor="reg-email">email:</label>
          <input
            id="reg-email"
            name="email"
            type="email"
            required
          />
        </p>

        <p>
          <label htmlFor="reg-password">Пароль:</label>
          <input
            id="reg-password"
            name="password"
            type="password"
            autoComplete="new-password"
            required
          />
        </p>

        <p>
          <label htmlFor="reg-role">Роль:</label>
          <select id="reg-role" name="role" required>
            <option value="USER">USER</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </p>

        <button type="submit" disabled={loading}>
          {loading ? "Регистрация" : "Зарегистрировать"}
        </button>
      </form>

      {message && <p>{message}</p>}
    </section>
  );
}

export default Regist;
