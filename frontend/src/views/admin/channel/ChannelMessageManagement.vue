<template>
    <el-card>
        <template #header>
            <span>频道信息管理</span>
        </template>
        <el-table :data="chatList" stripe>
            <el-table-column prop="id" label="ID" />
            <el-table-column prop="userId" label="发送者ID" />
            <el-table-column prop="username" label="发送者名称" />
            <el-table-column prop="channelId" label="频道ID" />
            <el-table-column prop="message" label="内容" />
            <el-table-column prop="createTime" label="发送时间">
                <template #default="scope">
                    {{ formatFollowTime(scope.row.createTime) }}
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="danger" @click="deleteMessage(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getUserNameByIdService } from '@/api/user';

import dayjs from 'dayjs';


import { getAllMessagesHistory, deleteMessageByIdService } from '@/api/message'
const chatList = ref([]);
const getchatList = async () => {
    let result = await getAllMessagesHistory();
    for (let i = 0; i < result.data.length; i++) {
        let message = result.data[i];
        let usernameResult = await getUserNameByIdService(message.userId);
        message.username = usernameResult.data;
    }
    chatList.value = result.data;
};

const deleteMessage = async (id) => {
    try {
        await deleteMessageByIdService(id);
        ElMessage.success('删除成功');
        getchatList();
    } catch (error) {
        ElMessage.error('删除失败');
    }
};

const formatFollowTime = (time) => {
    return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
};

getchatList();
</script>

<style scoped></style>