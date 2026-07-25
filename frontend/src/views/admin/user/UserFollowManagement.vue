<template>
    <el-card>
        <template #header>
            <span>用户关注管理</span>
        </template>
        <el-table :data="followList" stripe>
            <el-table-column prop="id" label="ID" />
            <el-table-column prop="followerId" label="关注者 ID" />
            <el-table-column prop="followerName" label="关注者名称" />
            <el-table-column prop="followeeId" label="被关注者 ID" />
            <el-table-column prop="followeeName" label="被关注者名称" />
            <el-table-column prop="followTime" label="关注时间">
                <template #default="scope">
                    {{ formatFollowTime(scope.row.followTime) }}
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="danger" @click="cancelFollow(scope.row.id)">取消</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getFollowListService, getUserNameByIdService, deleteFollowService } from '@/api/user';
import dayjs from 'dayjs';

const followList = ref([]);

const getFollowList = async () => {
    let result = await getFollowListService();
    for (let i = 0; i < result.data.length; i++) {
        let res = await getUserNameByIdService(result.data[i].followerId);
        let res1 = await getUserNameByIdService(result.data[i].followeeId);
        result.data[i].followerName = res.data;
        result.data[i].followeeName = res1.data;
    }
    followList.value = result.data;
};

const cancelFollow = async (id) => {
    try {
        await deleteFollowService(id);
        ElMessage.success('取消关注成功');
        getFollowList();
    } catch (error) {
        ElMessage.error('取消关注失败');
    }
};

const formatFollowTime = (time) => {
    return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
};

getFollowList();
</script>

<style scoped></style>