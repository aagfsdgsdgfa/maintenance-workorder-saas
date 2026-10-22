import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

export default defineConfig({
  plugins: [uni()],
  server: {
    port: 8082,
    host: '0.0.0.0',
    proxy: {
      '/zw': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/captchaImage': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/common': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/ai': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
