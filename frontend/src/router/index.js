import { createRouter, createWebHistory } from "vue-router";
//导入组件
import LoginVue from '@/views/Login.vue'
import InsectDetectionVue from '@/views/detect/InsectDetection.vue'
import DetectionHistoryVue from '@/views/detect/DetectionHistory.vue'
import ChangeUserInfoVue from '@/views/user/ChangeUserInfo.vue'
import ChangeUserPasswordVue from '@/views/user/ChangeUserPassword.vue'
import ChangeUserAvatarVue from '@/views/user/ChangeUserAvatar.vue'
import LayoutVue from '@/views/Layout.vue'
import UserCenterVue from "@/views/user/UserCenterVue.vue";
import Admin from "@/views/admin/Admin.vue";
import CommunicateVue from "@/views/communicate/CommunicateVue.vue";
import ArticleCreate from '@/views/article/ArticleCreate.vue';
import AdminIndex from '@/views/admin/index.vue';
import userManagement from '@/views/admin/user/UserManagement.vue';
import HistoryManagement from '@/views/admin/detect/HistoryManagement.vue';
import ArticleManagement from '@/views/admin/article/ArticleManagement.vue';
import UserProfile from "@/views/user/UserProfile.vue";
import ArticleDetail from "@/views/article/ArticleDetail.vue";
import Test from "@/views/article/Test.vue";
import ArticleIndex from "@/views/article/ArticleIndex.vue";
import ArticleEdit from "@/utils/ArticleEdit.vue";
import UserFollowManagement from "@/views/admin/user/UserFollowManagement.vue"
import UserChatManagement from "@/views/admin/user/UserChatManagement.vue"
import CategoryManagement from "@/views/admin/article/CategoryManagement.vue"
import articleCommentManagement from "@/views/admin/article/ArticleCommentManagement.vue"
import articleLikeManagement from "@/views/admin/article/ArticleLikeManagement.vue"
import articleCollectManagement from "@/views/admin/article/ArticleCollectManagement.vue"
import ChannelManagement from "@/views/admin/channel/ChannelManagement.vue"
import ChannelMessageManagement from "@/views/admin/channel/ChannelMessageManagement.vue"
import AdministratorManagement from "@/views/admin/user/AdministratorManagement.vue"
//定义路由关系
const routes = [
    { path: '/login', component: LoginVue },
    {
        path: '/articleindex',
        name: 'ArticleIndex',
        component: ArticleIndex
    },
    {
        path: '',
        // component:LayoutVue,
        // name: 'Test',
        component: LayoutVue,
        redirect: '/detect/insectdetection',
        children: [
            {
                path: 'detect/insectdetection',
                name: 'insectdetectionVue',
                component: InsectDetectionVue
            },
            {
                path: 'detect/detectionhistory',
                name: 'DetectionHistoryVue',
                component: DetectionHistoryVue
            },
            {
                path: 'user/UserCenterVue',
                name: 'UserCenterVue',
                component: UserCenterVue
            },
            {
                path: 'communicate/CommunicateVue',
                name: 'CommunicateVue',
                component: CommunicateVue
            },
            {
                path: 'user/info',
                name: 'ChangeUserInfoVue',
                component: ChangeUserInfoVue
            },
            {
                path: 'user/avatar',
                name: 'ChangeUserAvatarVue',
                component: ChangeUserAvatarVue
            },
            {
                path: 'user/resetpassword',
                name: 'ChangeUserPasswordVue',
                component: ChangeUserPasswordVue
            },
            {
                path: 'article/ArticleCreate',
                name: 'ArticleCreate',
                component: ArticleCreate
            },
            {
                path: 'article/:id',
                name: 'ArticleDetail',
                component: ArticleDetail
            },
            {
                path: 'article/:id',
                name: 'Test',
                component: Test
            },
            {
                path: 'user/userprofile/:userId',
                name: 'UserProfile',
                component: UserProfile,
            }
        ]
    },
    {
        path: '/admin',
        name: 'Layout',
        component: Admin,
        redirect: '/admin',
        children: [
            {
                path: '',
                name: 'AdminIndex',
                component: AdminIndex
            },
            {
                path: 'userManagement',
                name: 'userManagement',
                component: userManagement
            },
            {
                path: 'AdministratorManagement',
                name: 'AdministratorManagement',
                component: AdministratorManagement,
            },
            {
                path: 'ArticleManagement',
                name: 'ArticleManagement',
                component: ArticleManagement
            },
            {
                path: 'HistoryManagement',
                name: 'HistoryManagement',
                component: HistoryManagement
            },
            {
                path: 'ArticleEdit/:id',
                name: 'ArticleEdit',
                component: ArticleEdit
            },
            {
                path: 'UserFollowManagement',
                name: 'UserFollowManagement',
                component: UserFollowManagement
            },
            {
                path: 'UserChatManagement',
                name: 'UserChatManagement',
                component: UserChatManagement
            },
            {
                path: 'CategoryManagement',
                name: 'CategoryManagement',
                component: CategoryManagement
            },
            {
                path: 'ChannelMessageManagement',
                name: 'ChannelMessageManagement',
                component: ChannelMessageManagement
            },
            {
                path: 'articleCommentManagement',
                name: 'articleCommentManagement',
                component: articleCommentManagement
            },
            {
                path: 'articleLikeManagement',
                name: 'articleLikeManagement',
                component: articleLikeManagement
            },
            {
                path: 'articleCollectManagement',
                name: 'articleCollectManagement',
                component: articleCollectManagement
            },
            {
                path: 'ChannelManagement',
                name: 'ChannelManagement',
                component: ChannelManagement
            },
        ]
    },
]

//创建路由器
const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

//导出路由
export default router