<template>
    <el-card>
        <template #header>
            <span>文章点赞管理</span>
        </template>
        <el-table :data="likeList" stripe>
            <el-table-column prop="id" label="ID" />
            <el-table-column prop="userId" label="点赞者ID" />
            <el-table-column prop="name" label="点赞者昵称" />
            <el-table-column prop="articleId" label="文章ID" />
            <el-table-column prop="createTime" label="点赞时间">
                <template #default="scope">
                    {{ formatFollowTime(scope.row.createTime) }}
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="danger" @click="deleteLike(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getAllLikesService, cancelLikeByIdService } from '@/api/article';
import { getUserNameByIdService } from '@/api/user';
import dayjs from 'dayjs';

const likeList = ref([]);

const getlikeList = async () => {
    let result = await getAllLikesService();
    // console.log(result.data);
    for (let i = 0; i < result.data.length; i++) {
        let user = await getUserNameByIdService(result.data[i].userId);
        result.data[i].name = user.data;
    }
    // console.log(result.data)
    likeList.value = result.data;
};

const deleteLike = async (commentId) => {
    try {
        await cancelLikeByIdService(commentId);
        ElMessage.success('删除成功');
        getlikeList();
    } catch (error) {
        ElMessage.error('删除失败');
    }
};

const formatFollowTime = (time) => {
    // return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
    return new Date(time).toLocaleString();
};

getlikeList();
</script>

<style scoped>
.comment-content {
    word-wrap: break-word;
    word-break: break-all;
}
</style>