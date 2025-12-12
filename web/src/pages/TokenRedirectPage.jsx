import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { saveToken } from '../utils/tokenStorage';

function TokenRedirectPage() {
  const navigate = useNavigate();

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const accessToken = urlParams.get('accessToken');
    if (accessToken) {
      saveToken(accessToken);
      navigate('/projects', { replace: true });
    } else {
      navigate('/auth/login', { replace: true });
    }
  }, [navigate]);

  return null;
}

export default TokenRedirectPage;
