<template>
    <el-card>
        <template #header>
            <span>文章评论管理</span>
        </template>
        <el-table :data="commentList" stripe>
            <el-table-column prop="id" label="ID" />
            <el-table-column prop="userId" label="评论者ID" />
            <el-table-column prop="articleId" label="文章ID" />
            <el-table-column prop="content" label="评论内容" width="200">
                <template #default="scope">
                    <div class="comment-content">{{ scope.row.content }}</div>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="评论时间">
                <template #default="scope">
                    {{ formatFollowTime(scope.row.createTime) }}
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="danger" @click="deleteComment(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getAllCommentsService, deleteCommentService } from '@/api/article';
import dayjs from 'dayjs';

const commentList = ref([]);

const getcommentList = async () => {
    let result = await getAllCommentsService();
    commentList.value = result.data;
};

const deleteComment = async (comment) => {
    try {
        await deleteCommentService(comment);
        ElMessage.success('删除成功');
        getcommentList();
    } catch (error) {
        ElMessage.error('删除失败');
    }
};

const formatFollowTime = (time) => {
    return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
};

getcommentList();
</script>

<style scoped>
.comment-content {
    word-wrap: break-word;
    word-break: break-all;
}
</style>