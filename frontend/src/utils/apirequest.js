import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router';
import { useTokenStore } from '@/stores/token.js';

// 用于标记是否已经显示过服务异常提示
let isServiceErrorShown = false;
// 用于标记是否已经显示过 401 未登录提示
let is401ErrorShown = false;

// 封装创建 axios 实例的函数
const createAxiosInstance = (baseURL) => {
    const instance = axios.create({ baseURL });

    // 添加请求拦截器
    instance.interceptors.request.use(
        (config) => {
            // 请求前的回调
            // 添加 token
            const tokenStore = useTokenStore();
            // 判断有没有 token
            if (tokenStore.token) {
                config.headers.Authorization = tokenStore.token;
            }
            return config;
        },
        (err) => {
            // 请求错误的回调
            return Promise.reject(err);
        }
    );

    // 添加响应拦截器
    instance.interceptors.response.use(
        (result) => {
            // 判断业务状态码
            if (result.data.code === 0) {
                // 重置服务异常提示标记
                isServiceErrorShown = false;
                // 重置 401 未登录提示标记
                is401ErrorShown = false;
                return result.data;
            }
            // 操作失败
            if (!isServiceErrorShown) {
                ElMessage.error(result.data.message ? result.data.message : '服务异常');
                isServiceErrorShown = true;
            }
            // 异步操作的状态转换为失败
            return Promise.reject(result.data);
        },
        (err) => {
            // 判断响应状态码，如果为 401，则证明未登录，提示请登录，并跳转到登录页面
            if (err.response && err.response.status === 401) {
                if (!is401ErrorShown) {
                    ElMessage.error('请先登录');
                    router.push('/login');
                    is401ErrorShown = true;
                }
            } else {
                if (!isServiceErrorShown) {
                    ElMessage.error('服务异常');
                    isServiceErrorShown = true;
                }
            }
            return Promise.reject(err); // 异步的状态转化成失败的状态
        }
    );

    return instance;
};

// 创建不同 baseURL 的 axios 实例
export const apiInstance = createAxiosInstance('/api');
export const flaskInstance = createAxiosInstance('/flask');
export const chatInstance = createAxiosInstance('/chatapi');