import './assets/main.scss'
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from '@/router'
import { createPinia } from 'pinia'
import { createPersistedState } from 'pinia-persistedstate-plugin'
import App from './App.vue'
import './assets/css/global.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import './assets/iconfont/iconfont'
const pinia = createPinia()
const app = createApp(App)
const persist = createPersistedState();
pinia.use(persist)
app.use(router)
app.use(ElementPlus, {
    loading: {
        background: 'rgba(255, 255, 255, 0.5)',
        spinner: 'el-icon-loading',
        text: '加载中...',
    },
});
// 遍历并注册所有图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}
app.use(pinia)
app.mount('#app')
