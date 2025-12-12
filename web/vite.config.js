import fs from 'fs'
import dotenv from 'dotenv'
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

dotenv.config();

const keyPath = process.env.VITE_SSL_KEY
const certPath = process.env.VITE_SSL_CERT
const baseURL = process.env.VITE_BASE_URL
const host = process.env.VITE_HOST

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    host: host,
    port: 5173,
    https: {
      key: fs.readFileSync(keyPath),
      cert: fs.readFileSync(certPath),
    },
    proxy: {
      '/api': {
        target: baseURL,
        changeOrigin: true,
        secure: false,
      }
    }
  },
  resolve: {
    extensions: ['.js', '.jsx'],
  },
});
