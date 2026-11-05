import { useState, useEffect } from 'react'
import AuthContext from './AuthContext.jsx'

const API_BASE = import.meta.env.VITE_API_BASE;

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  async function checkSession() {
    try {
      const response = await fetch(
        `${API_BASE}/api/session/info`,
        {
          credentials: "include"
        }
      );

      if (!response.ok) {
        setUser(null);
        return false;
      }

      const data = await response.json();

      if (!data.isActive) {
        setUser(null);
        return false;
      }

      setUser(data);
      return true;

    } catch {
      setUser(null);
      return false;
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    checkSession();
  }, []);

  return (
    <AuthContext.Provider
      value={{ user, loading, checkSession }}
    >
      {children}
    </AuthContext.Provider>
  );
}