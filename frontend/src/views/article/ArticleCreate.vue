<template>
    <el-card class="articleCreateContainer">
        <div class="flex">
            <el-form :model="searchForm" ref="searchFormRef" label-width="80px">
                <div class="flex" style="margin-top: 10px;">
                    <el-form-item label="文章分类" style="width: 220px;">
                        <el-select v-model="searchForm.category" placeholder="请选择文章分类">
                            <el-option key="all" label="不限" value="all" />
                            <el-option v-for="category in categories" :key="category.id" :label="category.name"
                                :value="category.id" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="文章名称" style="width: 400px;">
                        <el-input v-model="searchForm.title" placeholder="请输入文章名称" />
                    </el-form-item>
                    <el-button style="margin-left: 10px;" type="primary" @click="handleSearch">搜索</el-button>
                    <el-button style="margin-left: 10px;" type="success" @click="openAddArticleDialog">增加文章</el-button>
                    <!-- <el-button style="margin-left: 10px;" type="success" @click="Test()">Test</el-button> -->
                </div>
            </el-form>
        </div>
        <div class="articleList-container">
            <div v-for="article in [...filteredArticles].reverse()" :key="article.id" class="articleList">
                <div class="flex" style="padding: 20px;">
                    <div class="listLeft">
                        <div class="userMessage" @click="goToUserProfile(article)">
                            <div class="avatar">
                                <img :src="findUserById(article.userId)?.avatarUrl || avatar" />
                            </div>
                            <div class="username">
                                {{ findUserById(article.userId)?.nickname || '未知用户' }}
                            </div>
                        </div>
                        <div class="">
                            <div class="articleTitle" @click="goToArticleDetail(article.id)">
                                {{ article.title }}
                            </div>
                            <div class="articleContent" v-html="processedContent(article)"
                                @click="goToArticleDetail(article.id)"></div>
                        </div>
                        <div class="articleFunction">
                            <el-icon style="color: #c5c5c5;margin-top: 3px;font-size: 18px;">
                                <View />
                            </el-icon>
                            <span style="margin-left: 2px;">
                                阅读
                                <span>{{ article.views }}</span>
                            </span>
                            <div id="like" @click="addLike(article.id)">
                                <el-icon style="margin-top: 2px;font-size: 18px;">
                                    <Pointer />
                                </el-icon>
                                <span style="margin-left: 2px;">点赞</span>
                                <span style="margin-left: 2px;">{{ article.likeCount }}</span>
                            </div>
                            <div id="collect" @click="addCollect(article.id)">
                                <el-icon style="margin-top: 2px;font-size: 18px;">
                                    <Star />
                                </el-icon>
                                <div style="display: flex;margin-left: 2px;">
                                    收藏
                                    <span style="margin-left: 2px;">{{ article.collectCount }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="articlePic">
                        <!-- 显示文章封面 -->
                        <img v-if="article.articleCoverUrl" :src="article.articleCoverUrl" alt="文章封面" />
                        <span v-else>暂无封面</span>
                    </div>
                </div>
            </div>
        </div>
        <!-- 新增文章弹窗 -->
        <el-dialog v-model="isAddArticleDialogVisible" title="发布文章" width="800px" @closed="handleEditorDestroy">
            <el-form :model="addArticleForm" ref="addArticleFormRef" label-width="80px">
                <el-form-item label="文章分类">
                    <el-select v-model="addArticleForm.categoryId" placeholder="请选择文章分类">
                        <el-option key="all" label="不限" value="all" />
                        <el-option v-for="category in categories" :key="category.id" :label="category.name"
                            :value="category.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="文章标题">
                    <el-input v-model="addArticleForm.title" placeholder="请输入文章标题" />
                </el-form-item>
                <el-form-item label="文章封面">
                    <el-upload ref="uploadRef" class="article-cover-uploader" :show-file-list="false"
                        :auto-upload="true" action="/api/file/upload" name="file"
                        :headers="{ 'Authorization': tokenStore.token }" :on-success="handleCoverUploadSuccess"
                        :on-error="handleCoverUploadError">
                        <img :src="addArticleForm.articlePic ? addArticleForm.articlePic : cover" class="article-cover"
                            width="200px" />
                    </el-upload>
                </el-form-item>
                <el-form-item label="文章内容">
                    <div style="border: 1px solid #ccc">
                        <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig"
                            style="border-bottom: 1px solid #ccc" />
                        <Editor v-model="editorContent" :defaultConfig="editorConfig"
                            style="height: 300px; overflow-y: hidden;" @onCreated="handleEditorCreated" />
                    </div>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="isAddArticleDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleAddArticleSubmit">发布</el-button>
            </template>
        </el-dialog>
    </el-card>
</template>

<script setup>
import { ref, onMounted, nextTick, onBeforeUnmount, shallowRef, } from 'vue';
import { useRouter } from 'vue-router';
import { getCategoriesInfoService } from '@/api/category.js';
import { articleInfoService, articleCreateService, addLikeService, isLikeService, isCollectService, collectService, cancelLikeService, cancelCollectService, addReadService } from '@/api/article.js';
import { userInfoService, getUsersService } from '@/api/user';
import { ElMessage } from 'element-plus';
import useUserInfoStore from '@/stores/userInfo.js';
import avatar from '@/assets/default.png';
import cover from '@/assets/cover.jpg';
import { View } from '@element-plus/icons-vue';
// 新增的wangEditor导入
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import { useTokenStore } from '@/stores/token.js';
import axios from 'axios'; // 引入axios用于发送上传请求

const tokenStore = useTokenStore();
const categories = ref([]);
const articles = ref([]);
const searchForm = ref({
    category: 'all',
    title: ''
});
const searchFormRef = ref(null);
const filteredArticles = ref([]);
const users = ref([]);
const userInfoStore = useUserInfoStore();
const router = useRouter();
// 富文本编辑器相关逻辑
const editorRef = shallowRef(null);
const editorContent = ref('<p></p>'); // 编辑器内容
const toolbarConfig = {
    excludeKeys: ['fullScreen'], // 排除不需要的菜单
};
// 新增计算属性处理内容
const processedContent = (article) => { // 改为方法
    // 提取第一个段落内容
    const firstParagraph = article.content
        .match(/<p[^>]*>([^<]+)<\/p>/i)?.[1] || '';

    // 处理剩余内容（可选）
    const remainingContent = article.content
        .replace(/<p[^>]*>[^<]+<\/p>/i, '')
        .replace(/<\/?p[^>]*>/g, ''); // 清理多余标签

    // 返回首行内容 + 省略号
    return `<span>${firstParagraph}</span><span class="ellipsis">...</span>`;
};
const editorConfig = {
    placeholder: '请输入文章内容...',
    MENU_CONF: {
        uploadImage: {
            server: '/api/file/upload', // 根据实际接口修改
            fieldName: 'file',
            maxFileSize: 30 * 1024 * 1024, // 30M
            allowedFileTypes: ['image/*'],
            headers: {
                'Authorization': tokenStore.token
            },
            customUpload: async (file, insertFn) => {
                try {
                    const formData = new FormData();
                    formData.append('file', file);
                    const response = await axios.post('/api/file/upload', formData, {
                        headers: {
                            'Authorization': tokenStore.token,
                            ...formData.getHeaders ? formData.getHeaders() : {}
                        }
                    });
                    if (response.data.code === 0) {
                        // 上传成功，插入图片到编辑器
                        insertFn(response.data.data);
                    } else {
                        throw new Error(response.data.message);
                    }
                } catch (error) {
                    console.error('图片上传失败', error);
                    ElMessage.error('图片上传失败');
                }
            }
        }
    }
};

// 文章封面上传相关
const addArticleForm = ref({
    categoryId: null, // 初始值设为null，避免默认值为字符串'all'
    title: '',
    content: '',
    articlePic: ''
});
const addArticleFormRef = ref(null);
const isAddArticleDialogVisible = ref(false);

const handleCoverUploadSuccess = (response, file, fileList) => {
    addArticleForm.value.articlePic = response.data;
    ElMessage.success('文章封面上传成功');
};

const handleCoverUploadError = (error, file, fileList) => {
    ElMessage.error('文章封面上传失败');
    console.error('文章封面上传错误：', error);
};

// 编辑器创建回调
const handleEditorCreated = (editor) => {
    editorRef.value = editor;
    // 将编辑器实例挂载到表单数据（可选）
    addArticleForm.value.editor = editor;
};
//摧毁富文本编辑器
const handleEditorDestroy = () => {
    nextTick(() => {
        if (!editorRef.value) return;

        try {
            const editor = editorRef.value;
            if (editor.isDestroyed) return;

            // 先获取DOM元素再销毁实例
            const editorContainer = editor.$el && editor.$el.parentElement; // 增加检查
            if (editorContainer) {
                editor.destroy();
                editorRef.value = null;

                // 精确清除DOM（新增防误删校验）
                if (editorContainer.classList.contains('w-e-full-screen-container')) {
                    editorContainer.remove();
                }
            }
        } catch (e) {
            console.error(' 安全捕获销毁异常:', e);
        }
    });
};

//表单提交处理
const handleAddArticleSubmit = async () => {
    try {
        // 获取富文本内容
        addArticleForm.value.content = editorContent.value;

        // 验证表单内容（示例）
        if (!addArticleForm.value.title.trim()) {
            ElMessage.warning(' 请输入文章标题');
            return;
        }

        // 处理分类id
        let categoryId = addArticleForm.value.categoryId;
        if (typeof categoryId === 'string' && categoryId === 'all') {
            categoryId = null;
        } else {
            categoryId = Number(categoryId);
        }

        // 提交逻辑（根据实际接口修改）
        const res = await articleCreateService({
            title: addArticleForm.value.title,
            content: addArticleForm.value.content,
            categoryId: categoryId,
            userId: userInfoStore.info.id,
            articleCoverUrl: addArticleForm.value.articlePic
        });

        ElMessage.success(' 发布成功');
        isAddArticleDialogVisible.value = false;
        // 刷新文章列表
        await getArticleInfo();
    } catch (error) {
        ElMessage.error(' 发布失败：' + error.message);
        console.error(' 发布文章失败:', error);
    }
};


const getCategoriesInfo = async () => {
    try {
        const result = await getCategoriesInfoService();
        categories.value = result.data.map(item => ({
            id: item.id,
            name: item.name,
            description: item.description
        }));
    } catch (error) {
        ElMessage.error('获取分类信息失败');
        console.error('获取分类信息时出错:', error);
    }
};

const getUserInfo = async () => {
    try {
        const result = await userInfoService();
        userInfoStore.info = result.data;
    } catch (error) {
        ElMessage.error('获取用户信息失败');
        console.error('获取用户信息时出错:', error);
    }
};

// 获取所有用户信息
const getAllUsers = async () => {
    try {
        const result = await getUsersService();
        users.value = result.data.map(item => ({
            id: item.id,
            username: item.username,
            nickname: item.nickname,
            email: item.email,
            avatarUrl: item.avatarUrl,
            money: item.money,
            createTime: item.createTime,
            updateTime: item.updateTime
        }));
    } catch (error) {
        console.error('获取用户信息失败:', error);
    }
};

// 获取文章信息
const getArticleInfo = async () => {
    try {
        const result = await articleInfoService();
        articles.value = result.data;
        filteredArticles.value = articles.value;
    } catch (error) {
        console.error('获取文章信息时出错:', error);
    }
};

// 根据用户 ID 查找用户信息
const findUserById = (userId) => {
    return users.value.find(user => user.id === userId);
};

// 处理搜索事件
const handleSearch = () => {
    const { category, title } = searchForm.value;
    if (category === 'all') {
        filteredArticles.value = articles.value;
    } else {
        filteredArticles.value = articles.value.filter((article) => {
            const categoryMatch = category ? article.categoryId === Number(category) : true;
            const titleMatch = title ? article.title.includes(title) : true;
            return categoryMatch && titleMatch;
        });
    }
};

// 跳转到用户详情页
const goToUserProfile = async (article) => {
    const userId = article.userId;
    const url = router.resolve({ name: 'UserProfile', params: { userId } }).href;
    window.open(url, '_blank');
};
// 跳转到文章详情页
const goToArticleDetail = async (articleId) => {
    //文章view+1
    let result = await addReadService(articleId);
    getArticleInfo();
    const url = router.resolve({ name: 'ArticleDetail', params: { id: articleId } }).href;
    window.open(url, '_blank');
};
// 打开新增文章弹窗
const openAddArticleDialog = () => {
    isAddArticleDialogVisible.value = true;
};


// 生命周期调整
onMounted(async () => {
    await Promise.all([
        getCategoriesInfo(),
        getUserInfo(),
        getArticleInfo(),
        getAllUsers()
    ]);
})

// 声明需要清理的资源
let pendingRequest = null;
let intervalId = null;
onBeforeUnmount(() => {
    // 取消未完成的网络请求
    if (pendingRequest) {
        pendingRequest.abort();
        pendingRequest = null;
    }

    // 清除定时器
    if (intervalId) {
        clearInterval(intervalId);
        intervalId = null;
    }

    // 清理第三方库实例（以编辑器为例）
    if (editorRef.value) {
        const editor = editorRef.value;
        if (!editor.isDestroyed) {
            const editorContainer = editor.$el && editor.$el.parentElement;
            if (editorContainer) {
                editor.destroy();
                editorRef.value = null;

                if (editorContainer.classList.contains('w-e-full-screen-container')) {
                    editorContainer.remove();
                }
            }
        }
    }
});

//点赞
const addLike = async (articleId) => {
    //判断是否已点赞
    const isLiked = await isLikeService(articleId);
    if (isLiked.data === "已点赞") {
        await cancelLikeService(articleId);
        getArticleInfo();
        ElMessage.success('取消点赞成功');
        return;
    } else {
        try {
            await addLikeService(articleId);
            getArticleInfo();
            ElMessage.success('点赞成功');
        }
        catch (error) {
            ElMessage.error('点赞失败');
            console.error('点赞失败:', error);
        }
    }


}
//收藏
const addCollect = async (articleId) => {
    //判断是否已收藏
    const isCollected = await isCollectService(articleId);
    if (isCollected.data === "已收藏") {
        await cancelCollectService(articleId);
        getArticleInfo();
        ElMessage.success('取消收藏成功');
    }
    else {
        try {
            await collectService(articleId);
            getArticleInfo();
            ElMessage.success('收藏成功');
        }
        catch (error) {
            ElMessage.error('收藏失败');
        }
    }
}
</script>

<style scoped>
@import '@wangeditor/editor/dist/css/style.css';

.articleCreateContainer {
    margin: auto;
    /* height: 700px; */
    /* border: 1px solid red; */
}

.articleList-container {
    /* height: 100%; */
    overflow-y: auto;
    border-top: 1px solid rgba(205, 203, 203, 0.5);
    /* border: 1px solid red; */

    /* &::-webkit-scrollbar {
        display: none;
    } */

    scrollbar-width: none;
}

.articleList {
    border-bottom: 1px solid rgba(205, 203, 203, 0.5);
    /* border: 1px solid red; */
}

.listLeft {
    width: 100%;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.articlePic {
    /* border: 1px solid red; */
    width: 150px;
    height: 90px;
    margin-top: 5px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-left: 10px;
}

.articlePic img {
    width: 100%;
    height: 100%;
    border-radius: 20px;
    border: 2px solid rgba(173, 173, 173, 0.445);
    object-fit: cover;
}

.avatar {
    margin-left: 5px;
    margin-right: 5px;
    width: 20px;
    height: 20px;
}

.avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border: 2px solid rgba(173, 173, 173, 0.445);
    border-radius: 50%;
}

.userMessage {
    display: flex;
    cursor: pointer;
}

.username {
    margin-left: 5px;
}

.username:hover {
    color: rgb(46, 110, 252);

}

.followBtn {
    margin-left: 5px;
}

.articleTitle {
    margin-top: 10px;
    margin-left: 5px;
    font-size: 21px;
    color: #000000;
    font-family: 微软雅黑体;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.articleTitle:hover {
    color: rgb(46, 110, 252);
    cursor: pointer;
}

.ellipsis {
    position: absolute;
    right: 0;
    padding-left: 5px;
    background: linear-gradient(to right, transparent, white);
}

.articleContent {
    height: 1.2em;
    line-height: 1.2em;
    overflow: hidden;
    position: relative;
    margin-top: 3px;
    font-family: "Microsoft YaHei", sans-serif;
    color: #919191;
    margin-left: 5px;
    font-size: 16px;
    cursor: pointer;
}

.articleFunction {
    /* border: 1px solid red; */
    margin-top: 5px;
    margin-left: 5px;
    display: flex;
    align-items: center;
    color: #919191;
    font-family: 微软雅黑体;
    cursor: default;
}

::v-deep .el-card__body {
    padding: 3px;
}

.article-cover-uploader {
    cursor: pointer;
    position: relative;
    overflow: hidden;
}

.article-cover {
    width: 200px;
    display: block;
}

#like {
    /* height: 10px; */
    cursor: pointer;
    margin-left: 10px;
    display: flex;
    letter-spacing: 2px;
}

#like:hover {
    color: black;
}

#collect {
    display: flex;
    margin-left: 10px;
    cursor: pointer;

}

#collect:hover {
    color: black;
}
</style>