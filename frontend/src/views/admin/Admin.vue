<template>
    <el-container class="layout-container">
        <!-- 左侧菜单 -->
        <el-aside width="200px">
            <el-menu default-active="/admin" active-text-color="#ffd04b" background-color="#232323" text-color="#fff"
                router>
                <el-menu-item index="/admin">
                    <el-icon>
                        <icons.Promotion />
                    </el-icon>
                    <span>网站后台首页</span>
                </el-menu-item>
                <el-sub-menu index="1">
                    <template #title>
                        <el-icon>
                            <icons.UserFilled />
                        </el-icon>
                        <span>网站用户管理</span>
                    </template>
                    <el-menu-item index="/admin/AdministratorManagement" v-if="adminTypes === '超级管理员'">
                        <el-icon>
                            <icons.Promotion />
                        </el-icon>
                        <span>管理员管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/userManagement">
                        <el-icon>
                            <icons.Promotion />
                        </el-icon>
                        <span>用户信息管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/UserFollowManagement">
                        <el-icon>
                            <icons.Promotion />
                        </el-icon>
                        <span>用户关注管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/UserChatManagement">
                        <el-icon>
                            <icons.Promotion />
                        </el-icon>
                        <span>私信记录管理</span>
                    </el-menu-item>
                </el-sub-menu>
                <el-sub-menu index="2">
                    <template #title>
                        <el-icon>
                            <icons.Promotion />
                        </el-icon>
                        <span>网站文章管理</span>
                    </template>
                    <el-menu-item index="/admin/CategoryManagement">
                        <el-icon>
                            <icons.Promotion />
                        </el-icon>
                        <span>文章分类管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/articleManagement">
                        <el-icon>
                            <icons.Promotion />
                        </el-icon>
                        <span>文章内容管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/articleCommentManagement">
                        <el-icon>
                            <icons.Promotion />
                        </el-icon>
                        <span>文章评论管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/articleLikeManagement">
                        <el-icon>
                            <icons.Promotion />
                        </el-icon>
                        <span>文章点赞管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/articleCollectManagement">
                        <el-icon>
                            <icons.Promotion />
                        </el-icon>
                        <span>文章收藏管理</span>
                    </el-menu-item>
                </el-sub-menu>
                <el-sub-menu>
                    <template #title>
                        <el-icon>
                            <icons.Promotion />
                        </el-icon>
                        <span>网站频道管理</span>
                    </template>
                    <el-menu-item index="/admin/ChannelManagement">
                        <el-icon>
                            <icons.Promotion />
                        </el-icon>
                        <span>频道信息管理</span>
                    </el-menu-item>
                    <el-menu-item index="/admin/ChannelMessageManagement">
                        <el-icon>
                            <icons.Promotion />
                        </el-icon>
                        <span>交流记录管理</span>
                    </el-menu-item>
                </el-sub-menu>
                <el-menu-item index="/admin/historyManagement">
                    <el-icon>
                        <icons.Promotion />
                    </el-icon>
                    <span>识别记录管理</span>
                </el-menu-item>
            </el-menu>
        </el-aside>
        <!-- 右侧主区域 -->
        <el-container>
            <!-- 头部区域 -->
            <el-header>
                <div>您好，管理员：<strong>{{ userInfoStore.info.username }}</strong></div>
                <el-dropdown placement="bottom-end" @command="handleCommand">
                    <span class="el-dropdown__box">
                        <el-avatar :src="userInfoStore.info.avatarUrl ? userInfoStore.info.avatarUrl : avatar" />
                        <el-icon>
                            <icons.CaretBottom />
                        </el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="back" :icon="icons.User">返回前台</el-dropdown-item>
                            <el-dropdown-item command="logout" :icon="icons.SwitchButton">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </el-header>
            <!-- 中间区域 -->
            <el-main>
                <router-view></router-view>
            </el-main>
            <!-- 底部区域 -->
            <el-footer>害虫识别系统 ©2025 |
                <a href="https://beian.miit.gov.cn/">粤ICP备2024250156号</a></el-footer>
        </el-container>
    </el-container>
</template>
<script setup>
import * as icons from '@element-plus/icons-vue';
import avatar from '@/assets/default.png'
import { ref } from 'vue';
import { userInfoService } from '@/api/user';
import useUserInfoStore from '@/stores/userInfo.js'
import { useTokenStore } from '@/stores/token.js'
const tokenStore = useTokenStore();
const userInfoStore = useUserInfoStore();
//调用函数,获取用户详细信息
const getUserInfo = async () => {
    //调用接口
    let result = await userInfoService();
    //数据存储到pinia中
    userInfoStore.setInfo(result.data);
}
getUserInfo();
//条目被点击后，调用函数
import { useRouter } from 'vue-router';
const router = useRouter();
import { ElMessage, ElMessageBox } from 'element-plus';
const handleCommand = (command) => {
    if (command === 'logout') {
        //退出登录
        ElMessageBox.confirm(
            '是否确认退出？',
            '温馨提示',
            {
                confirmButtonText: '确认',
                cancelButtonText: '取消',
                type: 'warning',
            }
        ).then(async () => {
            //退出
            //清空pinia中存储的token以及个人信息
            tokenStore.removeToken()
            userInfoStore.removeInfo()
            //跳转到登录页面
            router.push('/login')
            ElMessage({
                type: 'success',
                message: '退出登录成功',
            })
        }).catch(() => {
            ElMessage({
                type: 'info',
                message: '用户取消了退出登录',
            })
        })

    } else {
        if (command === 'back') {
            router.push('/')
            return
        }
        //路由
        router.push('/user/' + command)
    }
}
//查询管理员类型
import { getAdminTypeService } from '@/api/user';
const adminTypes = ref([]);
const getAdminType = async () => {
    let result = await getAdminTypeService(userInfoStore.info.id);
    adminTypes.value = result.data;
}
getAdminType();

</script>

<style scoped>
.layout-container {
    height: 100vh;

    .el-aside {
        background-color: #232323;

        &__logo {
            height: 50px;
            background: url('@/assets/logo2.png') no-repeat center / 180px auto;
        }

        .el-menu {
            border-right: none;
        }
    }

    .el-header {
        background-color: #fff;
        display: flex;
        align-items: center;
        justify-content: space-between;

        .el-dropdown__box {
            display: flex;
            align-items: center;

            .el-icon {
                color: #999;
                margin-left: 10px;
            }

            &:active,
            &:focus {
                outline: none;
            }
        }
    }

    .el-footer {
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        height: 30px;
        color: #666;
    }
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