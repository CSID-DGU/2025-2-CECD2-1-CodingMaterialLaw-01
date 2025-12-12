import axiosInstance from '../api/axiosInstance';
import { clearToken, saveToken } from '../utils/tokenStorage';

const BASE_URL = import.meta.env.VITE_BASE_URL;

export function loginWithProvider(provider) {
  window.location.href = `${BASE_URL}/oauth2/authorization/${provider}`;
}

export async function refreshAccessToken() {
  try {
    const res = await axiosInstance.post('/auth/refresh');
    const { accessToken } = res.data.data;
    saveToken(accessToken);
  } catch (err) {
    console.error('Refresh Token Error:', err);
    throw new Error(err.response?.data?.message || '토큰 갱신 실패');
  }
}

export async function logout() {
  try {
    await axiosInstance.post('/auth/logout');
    clearToken();
  } catch (err) {
    console.error('Logout Error:', err);
    throw new Error(err.response?.data?.message || '로그아웃 실패');
  }
}
