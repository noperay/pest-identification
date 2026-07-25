<template>
    <div class="w-full">
        <el-card class="z-card usermoney" :body-style="{ display: 'flex', padding: '0px !important' }">
            <template #header>
                <div class="header">
                    <span>我的关注（{{ friends.length }}）</span>
                </div>
            </template>
            <div class="friend-list">
                <div class="scrollable-container">
                    <div v-for="friend in friends" :key="friend.id" class="friend-item mb-4">
                        <div class="cursor-pointer" @click="viewFriendHome(friend.id)">
                            <div class="friendsContainerLayout">
                                <div class="friendsContainer flex">
                                    <div class="avatar">
                                        <img :src="friend.avatarUrl || defaultAvatar" alt="Avatar" />
                                    </div>
                                    <div class="nickname">{{ friend.nickname }}</div>
                                </div>
                            </div>
                            <el-divider></el-divider>
                        </div>
                    </div>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserInfoStore } from '@/stores/userInfo.js';
import { getFollowListService, getUserByIdService } from '@/api/user.js';
const router = useRouter();
const userInfo = useUserInfoStore();
// const followList = useFollowListStore();
const followList = ref([]);
const friends = ref([]);
const defaultAvatar = 'data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%2220%22%20height%3D%2220%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Ccircle%20cx%3D%2210%22%20cy%3D%2210%22%20r%3D%2210%22%20fill%3D%22%23ccc%22%2F%3E%3Ctext%20x%3D%2250%25%22%20y%3D%2250%25%22%20dominant-baseline%3D%22middle%22%20text-anchor%3D%22middle%22%20fill%3D%22white%22%3E%F0%9F%91%A4%3C%2Ftext%3E%3C%2Fsvg%3E';

// 查看好友主页
const viewFriendHome = (friendId) => {
    router.push(`/user/userprofile/${friendId}`);
};
const getFollowList = async () => {
    let result = await getFollowListService();
    followList.value = result.data;

    // 遍历 followList
    for (let item of followList.value) {
        if (item.followerId === userInfo.info.id) {
            await getUserByIdService(item.followeeId).then((res) => {
                friends.value.push(res.data);
            })

        }
    }
}
getFollowList();
</script>

<style scoped>
.flex {
    display: flex;
}

.col {
    flex-direction: column;
}

.w-full {
    height: 100%;
    width: 100%;
}

.friend-list {
    height: 550px;
    width: 100%;
    display: flex;
    flex-direction: column;
}

.cursor-pointer {
    width: 100%;
    margin-right: 10px;
}

.scrollable-container {
    /* height: 500px; */
    /* 设置一个固定高度，可根据实际情况调整 */
    overflow-y: auto;
    overflow-x: hidden
        /* 当内容超出高度时，显示垂直滚动条 */
}

.friendsContainerLayout {
    margin-right: 10px;
    width: 100%;
    border: 1px solid rgb(255, 255, 255);
    cursor: pointer;
    transition: background-color 0.2s;
}

.friendsContainerLayout:hover {
    background-color: #f0f0f0;
}

.friendsContainerLayout:active {
    background-color: #d0d0d0;
}

.friendsContainer {
    margin-top: 10px;
    margin-left: 10px;
    margin-right: 10px;
}

.avatar {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    overflow: hidden;
    margin-bottom: 0.5rem;
    margin-right: 10px;
    margin-left: 10px;
}

.avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.nickname {
    font-size: 15px;
    margin-top: 2px;
    /* border: 1px solid red; */
}

.el-divider {
    width: 100%;
    margin: 0;
}
</style>