<template>
    <el-card>
        <template #header>
            <span>识别记录管理</span>
        </template>
        <el-table :data="detectList" stripe>
            <el-table-column prop="id" label="ID" />
            <el-table-column prop="userId" label="识别者ID" />
            <el-table-column prop="username" label="识别者昵称" />
            <el-table-column prop="sort" label="种类" />
            <el-table-column prop="confidenceLevel" label="置信度" />
            <el-table-column prop="createTime" label="检测时间">
                <template #default="scope">
                    {{ formatFollowTime(scope.row.createTime) }}
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="primary" @click="showImageDialog(scope.row.imgUrl)">查看大图</el-button>
                    <el-button type="danger" @click="deleteDetect(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-dialog v-model="dialogVisible" title="查看大图">
            <img :src="dialogImageUrl" alt="识别图片" style="max-width: 100%;">
            <template #footer>
                <el-button @click="dialogVisible = false">关闭</el-button>
            </template>
        </el-dialog>
    </el-card>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getUserNameByIdService } from '@/api/user';
import { getAllDetectService, deleteDetectRecordService } from '@/api/detection';
import dayjs from 'dayjs';


const dialogVisible = ref(false);
const dialogImageUrl = ref('');
const detectList = ref([]);
const getdetectList = async () => {
    let result = await getAllDetectService();
    for (let i = 0; i < result.data.length; i++) {
        let message = result.data[i];
        let usernameResult = await getUserNameByIdService(message.userId);
        message.username = usernameResult.data;
    }
    detectList.value = result.data;
};

const deleteDetect = async (id) => {
    try {
        await deleteDetectRecordService(id);
        ElMessage.success('删除成功');
        getdetectList();
    } catch (error) {
        ElMessage.error('删除失败');
    }
};

const showImageDialog = (imageUrl) => {
    dialogImageUrl.value = imageUrl;
    dialogVisible.value = true;
};

const formatFollowTime = (time) => {
    return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
};

getdetectList();
</script>

<style scoped></style>