import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      '/api/auth': {
        target: 'http://localhost:8082',
        changeOrigin: true
      },
      '/api/results': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/api/marketing': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})
