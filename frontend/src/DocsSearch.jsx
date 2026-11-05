import { useState } from 'react';
import DocsPage from './DocsPage';
import { useAuth } from './UseAuth.jsx';


function DocsSearch() {
  const API_BASE = import.meta.env.VITE_API_BASE;
  const { user } = useAuth();
  const isAdmin = !!user && ["ADMIN", "SUPER_PUPER_ADMIN"].includes(user.role);
  const [searchParams, setSearchParams] = useState({
    title: '',
    author: '',
    year: '',
    description: '',
    type: '',
    language: ''
  });

  const [loading, setLoading] = useState(false);
  const [searchResults, setSearchResults] = useState(null);
  const [error, setError] = useState(null);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setSearchParams(prev => ({ ...prev, [name]: value }));
  };

  const handleClear = () => {
    setSearchParams({ title: '', author: '', year: '', description: '', type: '', language: '' });
    setSearchResults(null);
    setError(null);
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    const query = new URLSearchParams();
    Object.entries(searchParams).forEach(([key, value]) => {
      if (value.trim() !== '') {
        query.append(key, value.trim());
      }
    });

    try {
      const response = await fetch(`${API_BASE}/api/docs/get?${query.toString()}`, {
        method: 'GET',
        credentials: 'include'
      });

      if (!response.ok) {
        throw new Error(`ошибка сервера: ${response.status}`);
      }

      const data = await response.json();
      setSearchResults(data);
    } catch (err) {
      setError(err.message);
      setSearchResults(null);
    } finally {
      setLoading(false);
    }
  };

  const list = searchResults?.documents || searchResults?.data || (Array.isArray(searchResults) ? searchResults : null);

  return (
    <div>
      <form onSubmit={handleSearch}>
        <div>
          <input type="text" name="title" value={searchParams.title} onChange={handleInputChange} placeholder="Название" />
          <input type="text" name="author" value={searchParams.author} onChange={handleInputChange} placeholder="Автор" />
          <input type="number" name="year" value={searchParams.year} onChange={handleInputChange} placeholder="Год" />
        </div>
        
        <div>
          <input type="text" name="description" value={searchParams.description} onChange={handleInputChange} placeholder="Описание" />
          <input type="text" name="type" value={searchParams.type} onChange={handleInputChange} placeholder="Тип/Расширение" />
          <input type="text" name="language" value={searchParams.language} onChange={handleInputChange} placeholder="Язык" />
        </div>
      
        <button type="submit" disabled={loading}>Поиск</button>
        <button type="button" onClick={handleClear}>Очистить</button>
      </form>

      {error && <div>ошибка: {error}</div>}

      {list && list.length > 0 ? (
        <div>
          {list.map((doc) => (
            <DocsPage key={doc.id} doc={doc} isAdmin={isAdmin} />
          ))}
          <hr />
        </div>
      ) : searchResults ? (
        <p>ничего не найдено</p>
      ) : null}
    </div>
  );
}

export default DocsSearch;
