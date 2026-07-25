<template>
  <div class="nav-container">
    <div class="nav">
      <el-row>
        <el-col :span="12" :offset="6">
          <div class="logo-nav-wrapper">
            <div class="logo-wrapper">
              <img src="@/assets/logo.png" alt="Logo" class="logo">
            </div>
            <ul class="nav-menu">
              <li v-for="(link, index) in navLinks" :key="index" @click="navigateToRoute(link.text)" class="nav-item">
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
                  <el-dropdown-item @click="navigateToRoute('个人资料')"
                    v-if="userInfoStore.info.id !== 10077">个人资料</el-dropdown-item>
                  <el-dropdown-item v-if="userInfoStore.info.type === '超级管理员' || userInfoStore.info.type === '普通管理员'"
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
  <div class="main flex">
    <div class="viewsContainer flex">
      <el-scrollbar>
        <div class="left">
          <router-view :key="$route.fullPath"></router-view>
        </div>
      </el-scrollbar>
      <div class="right flex col">
        <div class="z-card usercon">
          <UserMessage :userId="userInfoStore.info.id"></UserMessage>
        </div>
        <div class="MyFollows">
          <MyFollows></MyFollows>
        </div>
      </div>
    </div>
  </div>
  <footer class="footer">
    <div class="footer-content">
      <div class="copyright">
        害虫识别交流网站 ©2025 |
        <a href="https://beian.miit.gov.cn/">粤ICP备2024250156号</a>
      </div>
    </div>
  </footer>
</template>
<script setup>
import { ref, watchEffect, onMounted } from 'vue';
import * as icons from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import UserMessage from "@/views/user/UserMessage.vue";
import MyFollows from "@/views/user/MyFollows.vue";
import { useRouter } from 'vue-router';
import { useTokenStore } from '@/stores/token.js';
import { useArticleStore } from '@/stores/articleInfo.js';
import { useUserListStore, useUserInfoStore, useFollowListStore } from '@/stores/userInfo.js';
const userInfoStore = useUserInfoStore();
const tokenStore = useTokenStore();
const userListStore = useUserListStore();
const followListStore = useFollowListStore();
const articleListStore = useArticleStore();
const router = useRouter();
const navLinks = [
  { name: 'insectdetectionVue', text: '害虫识别' },
  { name: 'DetectionHistoryVue', text: '识别历史' },
  { name: 'ArticleCreate', text: '网站文章' },
  { name: 'CommunicateVue', text: '交流频道' },
  { name: 'UserProfile', text: '用户中心' }
];
const navigateToRoute = (text) => {
  let routeName = '';
  switch (text) {
    case '害虫识别':
      routeName = 'insectdetectionVue';
      break;
    case '识别历史':
      routeName = 'DetectionHistoryVue';
      break;
    case '网站文章':
      routeName = 'ArticleIndex';
      break;
    case '交流频道':
      routeName = 'CommunicateVue';
      break;
    case '用户中心':
      routeName = 'UserProfile';
      break;
    case '个人资料':
      routeName = 'UserCenterVue';
      break;
  }

  // 导航到指定的路由，并传递 userId 参数 
  if (routeName === 'UserProfile') {
    if (!userInfoStore.info.id) {
      console.error('Missing  required parameter: userId');
      return;
    }

    router.push({
      name: routeName,
      params: { userId: userInfoStore.info.id }
    });
  } else {
    router.push({ name: routeName });
  }
}
const goToAdmin = () => {
  router.push('/admin')
}
const handleLogout = () => {
  ElMessageBox.confirm(
    '是否确认退出？',
    '温馨提示',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      // 清空 pinia 中存储的 token 以及个人信息
      tokenStore.removeToken();
      userInfoStore.removeInfo();
      userListStore.removeInfo();
      followListStore.removeInfo();
      articleListStore.removeInfo();
      // 跳转到登录页面
      await router.push('/login');

      ElMessage({
        type: 'success',
        message: '退出登录成功',
      });
    } catch (error) {
      // 捕获并处理在清空数据或路由跳转过程中可能出现的异常
      console.error('退出登录时出现错误:', error);
      ElMessage({
        type: 'error',
        message: '退出登录时出现错误',
      });
    }
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '用户取消了退出登录',
    });
  });
};
// console.log(userInfoStore.info)
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

.main-content {
  margin-top: 80px;
  padding: 20px;
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

.main {
  /* border: 1px solid red; */
  /* flex: 1; */
  height: auto;
  justify-content: center;
  width: 100%;
  margin-top: 80px;
  overflow-y: hidden;
  /* margin: 0 auto; */
  /* 使块级元素水平居中 */
}

.viewsContainer {

  margin: auto;
}

.logo-img {
  max-width: 200px;
  height: auto;
}

.left {
  /* height: 700px; */
  width: 1150px;
  margin-right: 20px;
  /* overflow-y: auto; */
  /* overflow-x: hidden; */
  /* border: 2px solid black; */
}

.right {
  width: 300px;
  /* height: 700px; */
  /* margin-right: 80px; */
  /* height: 1000px; */
  /* border: 1px solid black; */
}

.usercon {
  width: 100%;
}

.usermoney {
  margin-top: 20px;
  height: 150px;
}

.MyFollows {
  height: 700px;
  margin-top: 20px;
  margin-bottom: 20px;
}

.user-info {
  /* border: 1px solid red; */
  z-index: 1;
  position: absolute;
  right: 44px;
  top: 10px;
}

/* 添加以下样式 */
.el-dropdown {
  &:focus {
    outline: none;
    box-shadow: none;
  }

  position: relative;
}

.el-dropdown-link {
  &:focus {
    outline: none;
    box-shadow: none;
  }
}

.el-icon {
  color: #999;
  position: absolute;
  margin-left: 10px;
  margin-top: 13px;
  /* border: 1px solid red; */
}

.el-scrollbar__wrap {
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.el-scrollbar__wrap::-webkit-scrollbar {
  display: none;
}

.el-aside::-webkit-scrollbar {

  display: none;

}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: #f8f9fa;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  padding: 8px 0;
  z-index: 998;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  /* padding: 0 10px; */
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.footer-content a {
  color: #999;
  text-decoration: none;
  transition: color 0.3s ease;
}

.footer-content a:hover {
  color: #007bff;
}


.copyright {
  font-size: 12px;
  color: #999;
  text-align: center;
}

@media (max-width: 768px) {
  .footer-links {
    flex-direction: column;
    align-items: center;
    gap: 8px;
  }
}
</style>
