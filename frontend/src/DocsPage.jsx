import { useState } from 'react';

const API_BASE = import.meta.env.VITE_API_BASE;

function DocsPage({ doc, isAdmin = false }) {
  const [docData, setDocData] = useState(doc);
  const [prevDoc, setPrevDoc] = useState(doc);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState(null);
  const [isSaving, setIsSaving] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [isDeleted, setIsDeleted] = useState(false);
  const [error, setError] = useState(null);

  if (doc !== prevDoc) {
    setPrevDoc(doc);
    setDocData(doc);
    setIsEditing(false);
    setFormData(null);
    setError(null);
  }

  if (!docData) return null;
  if (isDeleted) return null;

  const handleDownload = async () => {
    try {
      const response = await fetch(`${API_BASE}/api/docs/download/${docData.id}`, {
        method: 'GET',
        credentials: 'include',
      });

      if (!response.ok) {
        throw new Error(`не удалось скачать файл: ${response.status}`);
      }

      const customFileName = `${docData.title}${docData.type}`;
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = customFileName;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      alert(`ошибка при скачивании: ${err.message}`);
    }
  };

  const handleEditClick = () => {
    setFormData({
      title: docData.title ?? '',
      author: docData.author ?? '',
      year: docData.year ?? '',
      description: docData.description ?? '',
      type: docData.type ?? '',
      language: docData.language ?? '',
    });
    setError(null);
    setIsEditing(true);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleCancel = () => {
    setIsEditing(false);
    setFormData(null);
    setError(null);
  };

  const handleSave = async () => {
    setError(null);

    let yearValue = null;
    if (formData.year !== '' && formData.year !== null) {
      yearValue = Number(formData.year);
      if (Number.isNaN(yearValue)) {
        setError('год должен быть числом');
        return;
      }
    }

    const payload = {
      title: formData.title,
      author: formData.author,
      year: yearValue,
      description: formData.description,
      type: formData.type,
      language: formData.language,
    };

    setIsSaving(true);
    try {
      const response = await fetch(`${API_BASE}/api/docs/update/${docData.id}`, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        let message = `ошибка: ${response.status}`;
        try {
          const body = await response.json();
          if (body?.error) message = body.error;
        } catch { /* ignore */ }
        throw new Error(message);
      }

      const updated = { ...docData, ...payload };
      setDocData(updated);
      setIsEditing(false);
      setFormData(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm(`Удалить документ "${docData.title}"?`)) {
      return;
    }

    setError(null);
    setIsDeleting(true);
    try {
      const response = await fetch(`${API_BASE}/api/docs/delete/${docData.id}`, {
        method: 'DELETE',
        credentials: 'include',
      });

      if (!response.ok) {
        let message = `ошибка: ${response.status}`;
        try {
          const body = await response.json();
          if (body?.error) message = body.error;
        } catch { /* ignore */ }
        throw new Error(message);
      }

      setIsDeleted(true);
    } catch (err) {
      setError(err.message);
    } finally {
      setIsDeleting(false);
    }
  };

  if (isEditing) {
    return (
      <div>
        <hr />
        <h3>Редактирование документа №{docData.id}</h3>

        <div>
          <label>
            Название:
            <input name="title" value={formData.title} onChange={handleChange} />
          </label>
        </div>
        <div>
          <label>
            Автор:
            <input name="author" value={formData.author} onChange={handleChange} />
          </label>
        </div>
        <div>
          <label>
            Год:
            <input name="year" type="number" value={formData.year} onChange={handleChange} />
          </label>
        </div>
        <div>
          <label>
            Описание:
            <input name="description" value={formData.description} onChange={handleChange} />
          </label>
        </div>
        <div>
          <label>
            Язык:
            <input name="language" value={formData.language} onChange={handleChange} />
          </label>
        </div>
        <div>
          <label>
            Тип файла:
            <input name="type" value={formData.type} onChange={handleChange} />
          </label>
        </div>

        {error && <p>ошибка: {error}</p>}

        <div>
          <button type="button" onClick={handleSave} disabled={isSaving}>
            {isSaving ? 'Сохранение' : 'Сохранить'}
          </button>
          <button type="button" onClick={handleCancel} disabled={isSaving}>
            Отмена
          </button>
        </div>
      </div>
    );
  }

  return (
    <div>
      <hr />
      <h3>
        <span>Файл: {docData.title}</span>, <span>автор: {docData.author}</span>,{' '}
        <span>год: {docData.year}</span>
      </h3>
      <p>Описание: {docData.description}</p>
      <div>
        Язык: {docData.language} | Тип файла: {docData.type} |{' '}
        <button type="button" onClick={handleDownload}>
          Скачать файл
        </button>
        {isAdmin && (
          <>
            <span> | </span>
            <button type="button" onClick={handleEditClick} disabled={isDeleting}>
              Редактировать
            </button>
            <span> | </span>
            <button type="button" onClick={handleDelete} disabled={isDeleting}>
              {isDeleting ? 'Удаление' : 'Удалить'}
            </button>
          </>
        )}
      </div>
      {error && <p>ошибка: {error}</p>}
    </div>
  );
}

export default DocsPage;
