<template>
    <div style="display: flex; justify-content: center;">
        <div class="nav-container">
            <div class="nav">
                <el-row>
                    <el-col :span="12" :offset="6">
                        <div class="logo-nav-wrapper">
                            <div class="logo-wrapper">
                                <img src="@/assets/logo.png" alt="Logo" class="logo">
                            </div>
                            <ul class="nav-menu">
                                <li v-for="(link, index) in navLinks" :key="index" @click="navigateToRoute(link.name)"
                                    class="nav-item">
                                    {{ link.text }}
                                </li>
                            </ul>
                        </div>
                    </el-col>
                    <el-col :span="1" :offset="5">
                        <div class="user-section">
                            <el-dropdown>
                                <div class="user-avatar-group">
                                    <el-avatar :size="40" :src="userInfoStore.info.avatarUrl" />
                                    <i class="el-icon-caret-bottom"></i>
                                </div>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item v-if="userInfoStore.info.id !== 10077"
                                            @click="navigateToRoute('UserCenterVue')">个人资料</el-dropdown-item>
                                        <el-dropdown-item v-if="userInfoStore.info.type === '超级管理员'"
                                            @click="goToAdmin">后台管理</el-dropdown-item>
                                        <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
        <div class="main-content">
            <div class="left">
                <el-card style="height: 400px;">
                    <template #header>热门文章</template>
                    <hotArticle />
                </el-card>
                <el-card class="hotAuthor" style="height: 400px;margin-top: 20px;">
                    <template #header>优质作者</template>
                    <hotAuthor />
                </el-card>
            </div>
            <div class="right">
                <ArticleCreate />
            </div>
        </div>
    </div>

</template>

<script setup>
import ArticleCreate from '@/views/article/ArticleCreate.vue';
import { useRouter } from 'vue-router';
import { useTokenStore } from '@/stores/token.js';
import { useUserInfoStore } from '@/stores/userInfo.js';
import { ElMessage, ElMessageBox } from 'element-plus';
import hotArticle from './HotArticle.vue';
import hotAuthor from './HotAuthor.vue';
const userInfoStore = useUserInfoStore();
const tokenStore = useTokenStore();
const router = useRouter();

const navLinks = [
    { name: 'insectdetectionVue', text: '害虫识别' },
    { name: 'DetectionHistoryVue', text: '识别历史' },
    { name: 'ArticleIndex', text: '网站文章' },
    { name: 'CommunicateVue', text: '交流频道' },
    { name: 'UserProfile', text: '用户中心' }
];

const navigateToRoute = (name) => {
    router.push({
        name: name,
        params: { userId: userInfoStore.info.id }
    });
};

const goToAdmin = () => {
    router.push('/admin');
};

const handleLogout = () => {
    ElMessageBox.confirm(
        '是否确认退出？',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            tokenStore.removeToken();
            userInfoStore.removeInfo();
            await router.push('/login');
            ElMessage.success('退出登录成功');
        } catch (error) {
            ElMessage.error('退出登录失败');
            console.error('退出错误:', error);
        }
    }).catch(() => {
        ElMessage.info('已取消退出');
    });
};
</script>

<style scoped>
html {
    box-sizing: border-box;
}

*,
*::before,
*::after {
    box-sizing: inherit;
}

.nav-container {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    z-index: 999;
    background-color: #fff;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.nav {
    /* border: 1px red solid; */
    height: 60px;
    /* padding: 0 30px; */
    margin: 0 auto;
}

.logo-nav-wrapper {
    margin-top: 5px;
    /* border: 1px red solid; */
    display: flex;
    align-items: center;
    gap: 30px;
    /* margin: auto; */
    justify-content: center;
}

.logo-wrapper {
    display: flex;
    align-items: center;
}

.logo {
    max-width: 200px;
    height: auto;
}

.nav-menu {
    /* border: 1px red solid; */
    display: flex;
    flex-shrink: 0;
    list-style: none;
    padding: 0;
    margin: 0;
    gap: 20px;
    justify-content: center;
}

.nav-item {
    cursor: pointer;
    font-size: 16px;
    color: #333;
    transition: color 0.3s ease;
    position: relative;
}

.nav-item::after {
    content: '';
    position: absolute;
    bottom: -5px;
    left: 0;
    width: 0;
    height: 2px;
    background-color: #007BFF;
    transition: width 0.3s ease;
}

.nav-item:hover {
    color: #007BFF;
}

.nav-item:hover::after {
    width: 100%;
}

.user-section {
    align-items: center;
    margin-top: 10px;
    margin-left: auto;
}

.user-avatar-group {
    align-items: center;
    cursor: pointer;
}

.el-icon-caret-bottom {
    font-size: 12px;
    color: #666;
}

@media (max-width: 768px) {
    .nav {
        flex-direction: column;
        height: auto;
        padding: 20px;
    }

    .logo-nav-wrapper {
        flex-direction: column;
        gap: 20px;
    }

    .nav-menu {
        flex-direction: column;
        align-items: center;
    }

    .user-section {
        margin-top: 20px;
        margin-left: 0;
    }
}

.main-content {
    display: flex;
    margin-top: 60px;
    padding: 20px;
}

.left {
    flex: 1;
    width: 300px;
    height: 100%;
    margin-right: 20px;
}

.right {
    /* padding: 10px; */
    /* border: 1px solid red; */
    flex: 3;
    width: 1100px;
    /* height: 100%; */
}

::v-deep .left .el-card__body {
    padding: 3px;
}

::v-deep .left .hotAuthor .el-card__body {
    padding: 0px;
}
</style>