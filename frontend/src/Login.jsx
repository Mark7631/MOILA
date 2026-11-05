import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from './UseAuth.jsx'

function Login() {
  const API_BASE = import.meta.env.VITE_API_BASE;

  const navigate = useNavigate();
  const { checkSession } = useAuth();

  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();

    const form = event.currentTarget;
    const formData = new FormData(form);

    const requestData = {
      email: formData.get("email"),
      password: formData.get("password")
    };

    setLoading(true);
    setMessage("");

    try {
      const response = await fetch(
        `${API_BASE}/api/log/in`,
        {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify(requestData)
        }
      );

      const bodyText = await response.text();

      if (!response.ok) {
        setMessage(`ошибка входа: ${response.status} ${bodyText}`);
        return;
      }

      const authenticated = await checkSession();

      if (authenticated) {
        form.reset();
        navigate("/", { replace: true });
      } else {
        setMessage("не удалось подтвердить сессию.");
      }

    } catch (error) {
      setMessage("ошибка запроса: " + error.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="login">
      <h2>Вход</h2>

      <form onSubmit={handleSubmit}>
        <p>
          <label htmlFor="login-email">Email:</label>
          <input
            id="login-email"
            name="email"
            type="email"
            required
          />
        </p>

        <p>
          <label htmlFor="login-password">Пароль:</label>
          <input
            id="login-password"
            name="password"
            type="password"
            autoComplete="current-password"
            required
          />
        </p>

        <button type="submit" disabled={loading}>
          {loading ? "Вход" : "Войти"}
        </button>
      </form>

      {message && <p>{message}</p>}
    </section>
  );
}

export default Login;
