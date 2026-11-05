import { useState } from 'react';

const NewFile = () => {
  const API_BASE = import.meta.env.VITE_API_BASE;

  const [formData, setFormData] = useState({
    title: '',
    author: '',
    year: '',
    description: '',
    type: '',
    language: ''
  });
  const [file, setFile] = useState(null);
  const [status, setStatus] = useState('');

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setStatus('');

    if (!formData.title || !formData.type || !file) {
      setStatus('заполните все поля и прикрепите файл');
      return;
    }

    const dataToSend = new FormData();
    dataToSend.append('title', formData.title);
    dataToSend.append('author', formData.author);
    dataToSend.append('year', formData.year);
    dataToSend.append('description', formData.description);
    dataToSend.append('type', formData.type);
    dataToSend.append('language', formData.language);
    dataToSend.append('file', file);

    try {
      const response = await fetch(`${API_BASE}/api/docs/put`, {
        method: 'POST',
        credentials: "include",
        body: dataToSend
      });

      if (response.ok) {
        setStatus('успешно отправлено');
        setFormData({ title: '', author: '', year: '', description: '', type: '', language: '' });
        setFile(null);
        e.target.reset();
      } else {
        const errorText = await response.text();
        setStatus(`ошибка сервера: ${errorText || response.statusText}`);
      }
    } catch (error) {
      setStatus(`ошибка сети: ${error.message}`);
    }
  };

  return (
    <div>
      <h2>Загрузка документа</h2>
      
      {status && <p>{status}</p>}

      <form onSubmit={handleSubmit}>
        <div>
          <label>Название </label>
          <input 
            type="text" 
            name="title" 
            value={formData.title} 
            onChange={handleInputChange} 
            maxLength={500} 
            required 
          />
          <span> | </span>
          <label>Автор </label>
          <input 
            type="text" 
            name="author" 
            value={formData.author} 
            onChange={handleInputChange} 
            maxLength={255} 
            required 
          />
          <span> | </span>
          <label>Год </label>
          <input 
            type="number" 
            name="year" 
            value={formData.year} 
            onChange={handleInputChange} 
            required 
          />
        </div>

        <div>
          <label>Описание </label>
          <textarea 
            name="description" 
            value={formData.description} 
            onChange={handleInputChange} 
            required 
          />
        </div>

        <div>
          <label>Тип </label>
          <input 
            type="text" 
            name="type" 
            value={formData.type} 
            onChange={handleInputChange} 
            maxLength={50} 
            required 
          />
          <span> | </span>
          <label>Язык </label>
          <input 
            type="text" 
            name="language" 
            value={formData.language} 
            onChange={handleInputChange} 
            maxLength={10} 
            required 
          />
        </div>

        <div>
          <label>Прикрепить файл </label>
          <input 
            type="file" 
            onChange={handleFileChange} 
            required 
          />
        </div>

        <button type="submit">Отправить</button>
      </form>
    </div>
  );
};

export default NewFile;
