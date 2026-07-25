<template>
    <div class="hotArticleContainer" v-for="author in sortedUserList" :key="author.id">
        <div class="articleTitle" @click="goToUserProfile(author.id)">
            <div class="avatar">
                <img :src="author.avatarUrl" />
            </div>
            <span style='margin-left:5px'>{{ author.nickname }}</span>
            <span  class="authorId">({{author.id}})</span>
            <el-icon class="viewIcon">
                <View />
            </el-icon>
            <div class="viewText"> {{ author.viewsCount }}</div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserListStore } from '@/stores/userInfo.js';
import { ElMessage } from 'element-plus';
import { getUsersService } from '@/api/user.js';

const userListStore = useUserListStore();
const router = useRouter();

// 从 userListStore 中获取用户列表
const userList = ref([]);

// 计算属性，对用户列表按 viewsCount 从高到低排序
const sortedUserList = computed(() => {
    return [...userList.value].filter(user => user.viewsCount > 1000).sort((a, b) => b.viewsCount - a.viewsCount);
});
const goToUserProfile = (userId) => {
    // 跳转到新页面的用户个人主页
    const url = `/user/userprofile/${userId}`;
    window.open(url, '_blank');
}
onMounted(() => {
    getUserList();
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
    userList.value = result.data;
};
</script>

<style scoped>
.hotArticleContainer {
    margin-left: 6px;
    align-items: center;
    display: flex;
    font-size: 17px;
}

.hotArticleContainer:hover {
    color: rgb(46, 110, 252);
    cursor: pointer;
}
.authorId{
    margin-top:2px;
    color:grey;
    font-size:15px;
}
.articleTitle {
    /* border:1px solid red; */
    height: 30px;
    display: flex;
    width: 100%;
    margin-top: 7px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.viewIcon {
    margin-left: 10px;
    color: #919191;
    margin-top: 3px;
    font-size: 15px;
}

.viewText {
    margin-top: 2px;
    margin-left: 5px;
    color: #919191;
    font-size: 14px;
}
.avatar {
    margin-left: 5px;
    margin-right: 5px;
    width: 25px;
    height: 25px;
}

.avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border: 2px solid rgba(173, 173, 173, 0.445);
    border-radius: 50%;
}
</style>    