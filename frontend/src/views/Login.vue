<template>
    <!-- 注册表单 -->
    <div class="loginpage flex col z-card">
        <div class="logo">
            <img src="@/assets/logo.png" alt="Logo" class="logo-img">
        </div>
        <el-form ref="form" size="large" autocomplete="off" v-if="isRegister" :model="registerData" :rules="rules">
            <el-form-item>
                <h1>注册</h1>
            </el-form-item>
            <el-form-item prop="username">
                <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="registerData.username"></el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input :prefix-icon="Lock" type="password" placeholder="请输入密码"
                    v-model="registerData.password"></el-input>
            </el-form-item>
            <el-form-item prop="rePassword">
                <el-input :prefix-icon="Lock" type="password" placeholder="请再次输入密码"
                    v-model="registerData.rePassword"></el-input>
            </el-form-item>
            <!-- 注册按钮 -->
            <el-form-item>
                <el-button class="button" type="primary" auto-insert-space @click="register">
                    注册
                </el-button>
            </el-form-item>
            <el-form-item class="flex">
                <el-link type="info" :underline="false" @click="isRegister = false; clearRegisterData()">
                    ← 返回
                </el-link>
            </el-form-item>
        </el-form>
        <!-- 登录表单 -->
        <el-form ref="form" size="large" autocomplete="off" v-else :model="registerData" :rules="rules">
            <el-form-item>
                <h1>登录</h1>
            </el-form-item>
            <el-form-item prop="username">
                <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="registerData.username"></el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input name="password" :prefix-icon="Lock" type="password" placeholder="请输入密码"
                    v-model="registerData.password"></el-input>
            </el-form-item>
            <el-form-item class="flex">
                <div class="flex">
                    <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
                </div>
            </el-form-item>
            <!-- 登录按钮 -->
            <el-form-item>
                <el-button class="button" type="primary" auto-insert-space @click="login">登录</el-button>
            </el-form-item>
            <el-form-item class="flex">
                <el-link type="info" :underline="false" @click="isRegister = true; clearRegisterData()">
                    注册
                </el-link>
            </el-form-item>
        </el-form>
        <el-footer>害虫识别系统 ©2025 |
            <a href="https://beian.miit.gov.cn/">粤ICP备2024250156号</a></el-footer>
    </div>

</template>
<script setup>
import { useUserListStore, useUserInfoStore, useFollowListStore } from '@/stores/userInfo.js';
import { articleInfoService } from '@/api/article.js';
import { useArticleStore } from '@/stores/articleInfo.js';
import { userInfoService, getUsersService, getFollowListService } from '@/api/user.js';
import { useTokenStore } from '@/stores/token.js';
import { User, Lock } from '@element-plus/icons-vue'
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userRegisterService } from '@/api/user.js'
import { userLoginService } from '@/api/user.js'
import { useRouter } from 'vue-router'
import { useWebSocketStore } from '@/stores/webSocketStore.js';
const webSocketStore = useWebSocketStore();
const router = useRouter()
const tokenStore = useTokenStore();
const userInfoStore = useUserInfoStore();
const userListStore = useUserListStore();
const followListStore = useFollowListStore();
const articleListStore = useArticleStore();
// 监听页面加载
onMounted(() => {
    const savedUsername = localStorage.getItem('username');
    const savedPassword = localStorage.getItem('password');
    if (savedUsername && savedPassword) {
        registerData.value.username = savedUsername;
        registerData.value.password = savedPassword;
        rememberMe.value = true;
    }

});
const getUserList = async () => {
    let result = await getUsersService();
    if (Array.isArray(result.data)) {
        result.data = result.data.map(item => {
            if (item.createTime && item.updateTime) {
                item.createTime = item.createTime.replace('T', ' ');
                item.updateTime = item.updateTime.replace('T', ' ');
            }
            return item;
        });
    }
    userListStore.setInfo(result.data);
};
const getUserInfo = async () => {
    let result = await userInfoService();
    if (result.data && result.data.createTime) {
        result.data.createTime = result.data.createTime.replace('T', ' ');
        result.data.updateTime = result.data.updateTime.replace('T', ' ');
    }
    userInfoStore.setInfo(result.data);
};
const getFollowList = async () => {
    let result = await getFollowListService();
    followListStore.setInfo(result.data);
}
const getArticleList = async () => {
    let result = await articleInfoService();
    if (Array.isArray(result.data)) {
        result.data = result.data.map(item => {
            if (item.createTime && item.updateTime) {
                // 截取前 10 个字符，即年月日部分
                item.createTime = item.createTime.slice(0, 10);
                item.updateTime = item.updateTime.slice(0, 10);
            }
            return item;
        });
    }
    articleListStore.setInfo(result.data);
};
// 控制注册与登录表单的显示，默认显示注册
const isRegister = ref(false)

// 定义数据模型
const registerData = ref({
    username: '',
    password: '',
    rePassword: ''
})

// 新增状态管理
const rememberMe = ref(false)

// 校验密码的函数
const checkRePassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次确认密码'))
    } else if (value !== registerData.value.password) {
        callback(new Error('请确保两次输入的密码一样'))
    } else {
        callback()
    }
}

// 定义表单校验规则
const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 5, max: 16, message: '长度为5~16位非空字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 5, max: 16, message: '长度为5~16位非空字符', trigger: 'blur' }
    ],
    rePassword: [
        { validator: checkRePassword, trigger: 'blur' }
    ]
}

// 完成注册
const register = async () => {
    let result = await userRegisterService(registerData.value);
    ElMessage.success(result.message ? result.message : '注册成功')
}

// 定义函数，清空数据模型的数据
const clearRegisterData = () => {
    registerData.value = {
        username: '',
        password: '',
        rePassword: ''
    }
}

// 完成登录
const login = async () => {
    let result = await userLoginService(registerData.value);
    getUserInfo();
    getUserList();
    getFollowList();
    getArticleList();
    ElMessage.success(result.msg ? result.msg : '登录成功');
    tokenStore.setToken(result.data);
    if (rememberMe.value) {
        localStorage.setItem('username', registerData.value.username);
        localStorage.setItem('password', registerData.value.password);
    } else {
        localStorage.removeItem('username');
        localStorage.removeItem('password');
    }
    router.push('/');
}

</script>

<style scoped>
.loginpage {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 20px;
    background-color: #f5f5f5;
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 100%;
    height: 100vh;
    overflow: hidden;

}

.logo {
    /* border: 1px solid red; */
    width: 100%;
    text-align: center;
    margin-bottom: 20px;
}

.logo-img {
    max-width: 400px;
    height: auto;
}

.el-form {
    /* border: 1px solid black; */
    width: 600px;
    padding: 30px;
    margin-top: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    background-color: white;
}

.form {
    display: flex;
    flex-direction: column;
    justify-content: center;
    user-select: none;
}

.title {
    margin: 0 auto;
}

.button {
    width: 100%;
}

.flex {
    width: 100%;
    display: flex;
    /* justify-content: space-between; */
}

.el-footer {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    height: 30px;
    color: #666;
}

.el-footer a {
    margin-left: 8px;
    color: #666;
    text-decoration: none;
    transition: color 0.3s ease;
}

.el-footer a:hover {
    color: #007bff;
}
</style>