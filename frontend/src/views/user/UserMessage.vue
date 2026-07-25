<template>
    <div class="user-main flex">
        <div class="avatar">
            <img :src="userInfo.info.avatarUrl ? userInfo.info.avatarUrl : avatar" />
        </div>
        <div class="usermessage">
            <div v-if="userInfo.info.nickname">昵称：{{ userInfo.info.nickname }}</div>
            <div v-else style="display: flex;">昵称：<div>（暂未设置）</div>
            </div>
            <div>用户id：{{ userInfo.info.id }}</div>
            <div>关注：{{ userInfo.info.follows }}</div>
            <div>粉丝：{{ userInfo.info.fans }}</div>
            <!-- <div>余额：{{ userInfo.info.money }}</div> -->
        </div>
    </div>
</template>
<script setup>
import { ref, onMounted, watch } from 'vue'
import avatar from '@/assets/default.png'
import useUserInfoStore from '@/stores/userInfo.js'
import { getUserByIdService } from '@/api/user.js'
const userInfo = useUserInfoStore();
const userMessageInfo = ref({});
userMessageInfo.value = userInfo.info;
onMounted(async () => {
    // let result = await getUserByIdService(userInfo.info.id);
    // console.log(result.data);
    // userInfo.info = result.data;
})
watch(userInfo, () => {
    // userMessageInfo.value = userInfo.info;
})
</script>
<style scoped>
.user-main {
    margin: 20px;
    /* border: 1px solid black; */
    display: flex;
}

.avatar {
    width: 100px;
    height: 100px;
    /* border: 1px solid rgb(255, 0, 0); */
    margin-right: 20px;
    overflow: hidden;
    flex-shrink: 0;
    /* 隐藏超出部分 */
}

.avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.usermessage {
    flex-direction: column;
    flex-shrink: 0;
    font-size: 15px;
    display: flex;
    font-family: 微软雅黑体;
}
</style>
