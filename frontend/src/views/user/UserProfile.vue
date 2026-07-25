<template>
    <div class="top z-card flex">
        <div class="avatar">
            <img :src="currentUserInfo.avatarUrl ? currentUserInfo.avatarUrl : avatar" />
        </div>
        <div class="userMessageBox flex col">
            <div class="flex">
                <div class="nickname" style="margin-top: 7px;">{{ currentUserInfo.nickname }}</div>
                <el-button v-if="!isFollow && currentUserInfo.id !== userInfo.info.id" class="button" type="info" plain
                    @click="followUser(currentUserInfo.id)">关注</el-button>
                <el-button v-else-if="currentUserInfo.id !== userInfo.info.id" class="button-cancel" type="info" plain
                    @click="CancelfollowUser(currentUserInfo.id)">取消关注</el-button>
            </div>
            <div class="flex col" style="font-size: 14px;margin-top: 5px;color: dimgrey;">
                <div class="userdetail flex">
                    <div style="font-weight: bold;margin-right: 7px;font-size: 15px;color: black;">{{
                        currentUserInfo.visitorCount }}</div>
                    总访问量<div style="margin-left: 7px;color:darkgrey;">|</div>
                    <div style="font-weight: bold;margin-right: 7px;font-size: 15px; margin-left: 7px;color: black;">
                        {{ currentUserInfo.articleCount }}
                    </div>
                    篇文章<div style="margin-left: 7px;color:darkgrey;">|</div>
                    <div style="font-weight: bold;margin-right: 7px;font-size: 15px; margin-left: 7px;color: black;">{{
                        currentUserInfo.fans }}
                    </div>
                    粉丝
                </div>
                <div class="flex" style="margin-top: 5px;">
                    加入时间：<div>{{ currentUserInfo.createTime }}</div>
                </div>
            </div>
        </div>
    </div>
    <div class="main flex">
        <div class="left flex col ">
            <el-card>
                <template #header>
                    热门文章
                </template>
                <div>
                    <UserHotArticle :userId="userId" />
                </div>

            </el-card>
            <el-card style="margin-top: 20px;">
                <template #header style="display: flex;">
                    <span class="TaChannel">TA的频道</span>
                    <el-link v-if="currentUserInfo.id === userInfo.info.id" class="btn-createChannel"
                        @click="goTocreateChannel()">新增频道</el-link>
                </template>
                <div>
                    <UserChannels ref="userChannelsRef" :userId="userId" />
                </div>
            </el-card>
        </div>
        <div class="right">
            <UserArticleLikeCollect />
        </div>
    </div>
    <el-dialog class="eldialog" width="370px" v-model="dialogVisible" title="新建频道">
        <div style="display: flex;">
            <div style="width: 150px;margin-top: 5px;">频道名称：</div>
            <el-input class="elinput" v-model="newChannel.channelName"></el-input>
            <el-button style="margin-left: 20px;" type="primary" @click="createChannel()">创建</el-button>
        </div>
        <div style="margin-top: 20px;">
            <span>频道状态：</span>
            <el-switch v-model="channalStatus" class="ml-2"
                style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949;margin-left: 10px;" />
        </div>
    </el-dialog>
</template>

<script setup>
import { ref, watchEffect, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage, ElButton } from 'element-plus';
import { followUserService, getMyFollowListService, cancelFollowUserService, getUserByIdService, getArticleCountsService, addUserVisitService } from '@/api/user.js';
import { useUserListStore, useUserInfoStore } from '@/stores/userInfo.js';
import UserArticleLikeCollect from '@/views/user/UserArticleLikeCollect.vue';
import UserHotArticle from '@/views/user/UserHotArticle.vue';
import UserChannels from './UserChannels.vue';
import { createChannelService } from '@/api/message.js'
const route = useRoute();
const userList = useUserListStore();
const userInfo = useUserInfoStore();
const avatar = '默认头像地址';
const currentUserInfo = ref({});
// const myFollows = ref([]);
const userId = route.params.userId;
// 根据路由参数获取用户信息
const getUserInfo = async () => {
    let result = await getUserByIdService(userId);
    currentUserInfo.value = result.data;
    //计算发布文章数量
    const articleCount = await getArticleCountsService(userId);
    currentUserInfo.value.articleCount = articleCount;
};
//关注用户
const followUser = async (userId) => {
    await followUserService(userId);
    isFollow.value = true;
    location.reload();
    userInfo.info.follows++;
    ElMessage({
        type: 'success',
        message: '关注成功',
    });
}
//取消关注
const CancelfollowUser = async (userId) => {
    await cancelFollowUserService(userId);
    isFollow.value = false;
    location.reload();
    userInfo.info.follows--;
    ElMessage({
        type: 'success',
        message: '取消关注成功',
    });
}

//查询是否关注
const isFollow = ref(false);
const init = async () => {
    let result = await getMyFollowListService(userId);
    if (result.data.length > 0) {
        isFollow.value = true;
    } else {
        isFollow.value = false;
    }
}
init();

// 监听路由变化，更新用户信息
watchEffect(() => {
    getUserInfo();
});
const dialogVisible = ref(false);
const newChannel = ref({});
const channalStatus = ref(true)
const userChannelsRef = ref(null);
//去创建频道
const goTocreateChannel = () => {
    dialogVisible.value = true;
    channalStatus.value = true;
}
//创建频道
const createChannel = async () => {
    if (channalStatus.value === true) {
        newChannel.value.status = "已启用"
    } else {
        newChannel.value.status = "已禁用"
    }
    await createChannelService(newChannel.value);
    userChannelsRef.value.findCreateChannel();
    ElMessage({
        type: 'success',
        message: '创建成功',
    });
    dialogVisible.value = false;
    newChannel.value = {};
}
const addUserVisit = async () => {
    await addUserVisitService(userId);
}
onMounted(() => {
    //用户访客增加
    addUserVisit();

})
</script>

<style scoped>
::v-deep .el-card__body {
    padding: 0px;
}

.main {
    margin-top: 20px;
    margin-bottom: 20px;
    /* height: 800px; */
}

.top {
    /* height: 100px; */
    padding: 10px;
}

.avatar {
    margin-left: 7px;
    width: 80px;
    height: 80px;
    border: 3px solid rgb(224, 224, 224);
    border-radius: 50%;
    margin-right: 20px;
    overflow: hidden;
}

.avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.userMessageBox {
    font-size: 16px;
}

.left {
    width: 320px;
    /* flex: 1; */
    /* padding: 10px; */
}

.right {
    width: 810px;
    /* height: 100; */
    background-color: white;
    /* height: 10000px; */
    /* border: 5px solid rgb(224, 224, 224); */
    margin-left: 20px;
    /* flex: 3; */
}

.button {
    margin-left: 7px;
    margin-top: 8px;
    height: 20px;
    width: 40px;
    font-size: 10px;
}

.button-cancel {
    margin-left: 7px;
    margin-top: 8px;
    height: 20px;
    width: 60px;
    font-size: 10px;

}

.btn-createChannel {
    font-size: 12px;
    margin-left: 10px;
}
</style>