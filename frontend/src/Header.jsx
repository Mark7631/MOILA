import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from './UseAuth.jsx'

function Header({ email, isAdmin = false  }) {
  const API_BASE = import.meta.env.VITE_API_BASE;

  const navigate = useNavigate();
  const { checkSession } = useAuth();

  async function handleLogout() {
    try {
      const response = await fetch(`${API_BASE}/api/log/out`, {
        method: "POST",
        credentials: "include"
      });

      if (!response.ok) {
        alert("не удалось выйти из аккаунта");
        return;
      }

      await checkSession();
      navigate("/login", { replace: true });
    } catch (error) {
      alert("ошибка выхода: " + error.message);
    }
  }

  return (
    <header>
      <span>Архив</span>
      <span> | </span>
      <Link to="/">Главная</Link>
      <span> | </span>
      {isAdmin && (
          <>
            <Link to="/admin">Админ панель</Link>
            <span> | </span>
          </>
        )}
      <Link to="/login">Вход/Смена пароля</Link>
      <span> | </span>
      <span>{email}</span>
      <span> | </span>
      <button type="button" onClick={handleLogout}>
        Выйти
      </button>
      <hr />
    </header>
  );
}

export default Header;
