<template>
    <div class="article-detail-container">
        <el-card class="article-card">
            <template #header>
                <span class="article-header-title">{{ articleDetail.title }}</span>
            </template>
            <div v-if="articleDetail.title" class="article-content-wrapper">
                <div class="article-meta">
                    <span class="article-meta-item">作者: {{ authorInfo.nickname }}</span>
                    <span class="article-meta-item">发布时间: {{ formattedCreateTime }}</span>
                    <span class="article-meta-item"> 浏览量: {{ articleDetail.views }}</span>
                    <span class="article-meta-item"> 点赞: {{ articleDetail.likeCount }}</span>
                    <span class="article-meta-item"> 收藏: {{ articleDetail.collectCount }}</span>
                </div>
                <div class="article-body" v-html="articleDetail.content"></div>
            </div>
            <div v-else class="article-loading">
                加载中...
            </div>
        </el-card>
        <el-card class="comment-card">
            <template #header>
                <span class="article-header-title">评论</span>
            </template>
            <div class="article-comment-wrapper">
                <el-form :model="commentForm" ref="commentFormRef" label-width="80px">
                    <el-form-item label="评论内容" prop="content">
                        <el-input v-model="commentForm.content" type="textarea" placeholder="请输入评论内容"
                            :rows="3"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleSubmitComment">提交评论</el-button>
                    </el-form-item>
                </el-form>
                <div class="article-comments" v-if="comments.length > 0">
                    <div v-for="(comment, index) in comments" :key="index" class="comment-item">
                        <div class="comment-meta">
                            <span>
                                <span class="comment-author">{{ comment.author }}：</span>
                                <span>{{ comment.content }}</span>
                            </span>
                            <span v-if="canDeleteComment(comment)" class="comment-delete">
                                <el-icon @click="handleDeleteComment(comment)">
                                    <Delete />
                                </el-icon>
                            </span>
                        </div>
                        <span class="comment-time">{{ comment.createTime }}</span>
                    </div>
                </div>
                <div v-else-if="articleDetail.title && comments.length === 0" class="no-comments">
                    暂无评论
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { articleDetailService, articleCommentService, createCommentService, deleteCommentService } from '@/api/article.js';
import { getUserByIdService } from '@/api/user.js';
import { ElMessage, ElMessageBox } from 'element-plus';
import useUserInfoStore from '@/stores/userInfo.js';
import { Delete } from '@element-plus/icons-vue';

const userInfoStore = useUserInfoStore();
const route = useRoute();
const articleId = route.params.id;
const articleDetail = ref({});
const authorInfo = ref({});
const comments = ref([]);
const commentForm = ref({
    content: ''
});
const commentFormRef = ref(null);

const canDeleteComment = (comment) => {
    return userInfoStore.info.id === articleDetail.value.userId || userInfoStore.info.id === comment.userId;
};

const handleDeleteComment = async (comment) => {
    try {
        // 显式获取action，处理解构错误
        const response = await ElMessageBox.confirm(
            '确认要删除这条评论吗？',
            '删除确认',
            {
                confirmButtonText: '删除',
                cancelButtonText: '取消',
                type: 'warning'
            }
        );
        const action = response?.action || response; // 兼容不同版本解构方式
        if (action === 'confirm') {
            // 确保服务端接收整个对象（需要后端支持）
            await deleteCommentService(comment);
            ElMessage.success('评论删除成功');
            await fetchArticleComments();
        }
    } catch (error) {
        // 处理用户取消操作
        if (error && error.name === 'CancelError') {
            console.log('用户取消了删除操作');
            return;
        }

        // 处理其他异常
        ElMessage.error('删除评论失败: ' + error.message);
        console.error('删除评论时出错:', error);
    }
};

const formattedCreateTime = computed(() => {
    if (articleDetail.value.createTime) {
        const date = new Date(articleDetail.value.createTime);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }
    return '';
});

const fetchArticleDetail = async () => {
    try {
        const result = await articleDetailService(articleId);
        articleDetail.value = result.data;
    } catch (error) {
        console.error('获取文章详情时出错:', error);
    }
};

const fetchAuthorInfo = async () => {
    if (articleDetail.value.userId) {
        try {
            const result = await getUserByIdService(articleDetail.value.userId);
            authorInfo.value = result.data;
        } catch (error) {
            console.error('获取用户信息时出错:', error);
        }
    }
};

const fetchArticleComments = async () => {
    try {
        const result = await articleCommentService(articleId);
        for (let i = 0; i < result.data.length; i++) {
            const userResult = await getUserByIdService(result.data[i].userId);
            result.data[i].author = userResult.data.nickname;
            const commentDate = new Date(result.data[i].createTime);
            const year = commentDate.getFullYear();
            const month = String(commentDate.getMonth() + 1).padStart(2, '0');
            const day = String(commentDate.getDate()).padStart(2, '0');
            result.data[i].createTime = `${year}-${month}-${day}`;
            result.data[i].userId = userResult.data.id;
        }
        comments.value = result.data;
    } catch (error) {
        console.error('获取文章评论时出错:', error);
    }
};

const handleSubmitComment = async () => {
    if (!userInfoStore.info.id) {
        ElMessage.error('请先登录再发表评论');
        return;
    }
    try {
        await commentFormRef.value.validate();
        const newComment = {
            articleId: articleId,
            content: commentForm.value.content,
            userId: userInfoStore.info.id
        };
        const res = await createCommentService(newComment);
        ElMessage.success('评论提交成功');
        commentForm.value.content = '';
        await fetchArticleComments();
    } catch (error) {
        ElMessage.error('评论提交失败: ' + error.message);
        console.error('提交评论时出错:', error);
    }
};

const initiate = async () => {
    await fetchArticleDetail();
    await fetchAuthorInfo();
    await fetchArticleComments();
};

onMounted(() => {
    initiate();
});
</script>

<style scoped>
::v-deep .article-card .el-card__body {
    padding: 0px;
}

.article-detail-container {
    padding-bottom: 20px;
}

.article-card {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
}

.article-header-title {
    font-size: 20px;
    font-weight: bold;
}

.article-content-wrapper {
    padding: 20px;
}

.article-meta {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
    color: #666;
}

.article-meta-item {
    margin-right: 20px;
}

.article-body {
    white-space: pre-line;
    text-indent: 1em;
    line-height: 1.6;
}

.article-loading {
    padding: 20px;
    text-align: center;
    color: #999;
}

.article-comments {
    margin-top: 20px;
}

.comment-card {
    margin-top: 20px;
}

.comments-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 10px;
}

.comment-item {
    border: 1px solid #eee;
    border-radius: 4px;
    padding: 10px;
    margin-bottom: 10px;
}

.comment-meta {
    display: flex;
    justify-content: space-between;
    color: #666;
    margin-bottom: 5px;
}

.comment-author {
    font-weight: bold;
}

.comment-time {
    font-size: 12px;
}

.no-comments {
    margin-top: 20px;
    color: #999;
}

.comment-form {
    margin-bottom: 20px;
}

.comment-delete {
    color: #ff4949;
    cursor: pointer;
    margin-left: 10px;
}

.comment-delete:hover {
    color: #ff7a7a;
}
</style>