import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL;
const axiosInstance = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true 
});

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    } else {
      console.warn('[axios] 토큰 없음: Authorization 헤더 미설정');
    }
    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      console.warn('인증 만료됨 - 로그인 필요');
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
