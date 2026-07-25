<template>
    <el-card>
        <template #header>
            <span>文章收藏管理</span>
        </template>
        <el-table :data="collectList" stripe>
            <el-table-column prop="id" label="ID" />
            <el-table-column prop="userId" label="收藏者ID" />
            <el-table-column prop="name" label="点赞者昵称" />
            <el-table-column prop="articleId" label="文章ID" />
            <el-table-column prop="createTime" label="收藏时间">
                <template #default="scope">
                    {{ formatFollowTime(scope.row.createTime) }}
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="danger" @click="deleteCollect(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getAllCollectsService, cancelCollectByIdService } from '@/api/article';
import { getUserNameByIdService } from '@/api/user';
import dayjs from 'dayjs';

const collectList = ref([]);

const getcollectList = async () => {
    let result = await getAllCollectsService();
    // console.log(result.data);
    for (let i = 0; i < result.data.length; i++) {
        let user = await getUserNameByIdService(result.data[i].userId);
        result.data[i].name = user.data;
    }
    // console.log(result.data)
    collectList.value = result.data;
};

const deleteCollect = async (id) => {
    try {
        await cancelCollectByIdService(id);
        ElMessage.success('删除成功');
        getcollectList();
    } catch (error) {
        ElMessage.error('删除失败');
    }
};

const formatFollowTime = (time) => {
    return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
};

getcollectList();
</script>

<style scoped>
.comment-content {
    word-wrap: break-word;
    word-break: break-all;
}
</style>