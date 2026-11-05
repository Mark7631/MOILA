import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'

import Home from './Home.jsx'
import LogPage from './LogPage.jsx'
import ProtectedRoute from './ProtectedRoute.jsx'
import { AuthProvider } from './AuthProvider.jsx'
import AdminPanel from './AdminPanel.jsx'

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LogPage />} />

          <Route element={<ProtectedRoute />}>
            <Route path="/" element={<Home />} />
            <Route path="/admin" element={<AdminPanel />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App;
