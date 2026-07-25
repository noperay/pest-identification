<template>
    <el-card>
        <template #header>
            <span>用户私信管理</span>
        </template>
        <el-table :data="chatList" stripe>
            <el-table-column prop="id" label="ID" />
            <el-table-column prop="senderId" label="发送者ID" />
            <el-table-column prop="senderName" label="发送者名称" />
            <el-table-column prop="receiverId" label="接收者ID" />
            <el-table-column prop="receiverName" label="接收者名称" />
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
import { getUserChatListService, deleteMessageService } from '@/api/user';
import dayjs from 'dayjs';

const chatList = ref([]);

const getchatList = async () => {
    let result = await getUserChatListService();
    chatList.value = result.data;
};

const deleteMessage = async (id) => {
    try {
        await deleteMessageService(id);
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