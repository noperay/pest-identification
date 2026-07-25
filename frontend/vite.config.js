import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { NodeGlobalsPolyfillPlugin } from '@esbuild-plugins/node-globals-polyfill';
import { NodeModulesPolyfillPlugin } from '@esbuild-plugins/node-modules-polyfill';
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      // crypto: 'crypto-browserify',

    }
  },
  server: {
    proxy: {
      '/api': {//获取路径中包含了/api的请求
        // target: 'http://106.52.231.216:8082',
        target: 'http://localhost:8080',
        changeOrigin: true,//修改源
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/flask': {
        target: 'http://localhost:5000',
        // target: 'http://106.52.231.216:5000',
        changeOrigin: true,//修改源
        rewrite: (path) => path.replace(/^\/flask/, '')
      },
      // '/chatapi': {
      //   target: 'https://api.yyy001.com',
      //   changeOrigin: true,//修改源
      //   rewrite: (path) => path.replace(/^\/chatapi/, '')
      // },
    }
  },
  optimizeDeps: {
    esbuildOptions: {
      plugins: [
        NodeGlobalsPolyfillPlugin({
          buffer: true,
          // crypto: true
        }),
        NodeModulesPolyfillPlugin()
      ]
    }
  },
})
