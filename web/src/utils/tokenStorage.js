export function saveToken(accessToken) {
  localStorage.setItem('accessToken', accessToken);
}

export function clearToken() {
  localStorage.removeItem('accessToken');
}

export function getAccessToken() {
  return localStorage.getItem('accessToken');
}
