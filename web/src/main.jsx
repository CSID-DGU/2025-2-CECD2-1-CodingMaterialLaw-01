import ReactDOM from 'react-dom/client';
import App from './App';
import { BrowserRouter } from 'react-router-dom';
import './index.css';

if (typeof window !== 'undefined' && '__VUE_DEVTOOLS_GLOBAL_HOOK__' in window) {
  try {
    Object.defineProperty(window, '__VUE_DEVTOOLS_GLOBAL_HOOK__', {
      value: {},
      writable: false,
      configurable: false,
    });
  } catch (e) {
    console.warn('Vue Devtools hook override failed:', e);
  }
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
);
