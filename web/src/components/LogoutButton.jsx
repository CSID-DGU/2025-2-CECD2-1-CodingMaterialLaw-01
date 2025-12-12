// src/components/LogoutButton.jsx
import { useNavigate } from 'react-router-dom';
import { clearToken } from '../utils/tokenStorage';
import { LogOut } from 'lucide-react';
import axios from 'axios';

function LogoutButton() {
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      const accessToken = localStorage.getItem('accessToken');

      await axios.post(
        `${import.meta.env.VITE_BASE_URL}/api/auth/logout`,
        {},
        {
          headers: {
            'Authorization': `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
          },
          withCredentials: true, // 쿠키를 사용하는 경우 필요
        }
      );
    } catch (error) {
      console.warn('로그아웃 요청 실패:', error.message);
    } finally {
      clearToken();
      navigate('/auth/login');
    }
  };

  return (
    <button
      onClick={handleLogout}
      title="Logout"
      className="fixed top-4 right-4 z-50 flex flex-col items-center text-gray-500 hover:text-red-500"
    >
      <LogOut size={24} />
      <span className="text-xs mt-1">Logout</span>
    </button>
  );
}

export default LogoutButton;
