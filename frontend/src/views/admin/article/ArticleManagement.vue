<template>
    <el-card class="ArticleManagementContainer">
        <template #header>
            <div class="clearfix">
                <div class="flex">
                    <el-form :model="searchForm" ref="searchFormRef" label-width="80px">
                        <div class="flex"
                            style="margin-top: 10px;align-items: center;justify-content: center;justify-items: center;">
                            <el-form-item label="文章分类" style="width: 200px;">
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
                            <el-button style="margin-left: 10px;" type="success"
                                @click="openAddArticleDialog">增加文章</el-button>
                        </div>
                    </el-form>
                </div>
            </div>
        </template>
        <el-table :data="filteredArticles" stripe fit>
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="title" label="标题" min-width="150"></el-table-column>
            <el-table-column prop="categoryName" label="分类" width="100"></el-table-column>
            <el-table-column prop="userId" label="用户 ID" width="100"></el-table-column>
            <el-table-column prop="views" label="阅读量" width="100"></el-table-column>
            <el-table-column prop="likeCount" label="点赞数" width="100"></el-table-column>
            <el-table-column prop="commentCount" label="评论数" width="100"></el-table-column>
            <el-table-column prop="collectCount" label="收藏数" width="100"></el-table-column>
            <el-table-column prop="status" label="状态" width="100"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180">
                <template #default="{ row }">
                    {{ formatDate(row.createTime) }}
                </template>
            </el-table-column>
            <el-table-column prop="updateTime" label="更新时间" width="180">
                <template #default="{ row }">
                    {{ formatDate(row.updateTime) }}
                </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
                <template #default="{ row }">
                    <el-button type="primary" size="small" @click="editArticle(row.id)">修改</el-button>
                    <el-button type="danger" size="small" @click="deleteArticle(row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
            :page-sizes="[10, 20, 30]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
            :total="totalArticles"></el-pagination> -->

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
                        <img :src="addArticleForm.articleCoverUrl ? addArticleForm.articleCoverUrl : cover"
                            class="article-cover" width="200px" />
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
import { ref, onMounted, nextTick, onBeforeUnmount, shallowRef } from 'vue';
import { useRouter } from 'vue-router';
import { getCategoriesInfoService } from '@/api/category.js';
import { articleInfoService, articleCreateService, articleDeleteService } from '@/api/article.js';
import { useTokenStore } from '@/stores/token.js';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import { ElMessage } from 'element-plus';
import cover from '@/assets/cover.jpg';
import { getCategoryInfoByIdService } from "@/api/category.js";
import { useUserInfoStore } from "@/stores/userInfo.js";
const tokenStore = useTokenStore();
const router = useRouter();
const userInfoStore = useUserInfoStore();
// 新增文章相关
const addArticleForm = ref({
    categoryId: null,
    title: '',
    content: '',
    articleCoverUrl: ''
});
const addArticleFormRef = ref(null);
const isAddArticleDialogVisible = ref(false);
const editorRef = shallowRef(null);
const editorContent = ref('<p></p>');
const toolbarConfig = { excludeKeys: ['fullScreen'] };
const editorConfig = {
    placeholder: '请输入文章内容...',
    MENU_CONF: {
        uploadImage: {
            server: '/api/file/upload',
            fieldName: 'file',
            headers: { 'Authorization': tokenStore.token },
            customUpload: async (file, insertFn) => {
                try {
                    const formData = new FormData();
                    formData.append('file', file);
                    const response = await articleCreateService(formData);
                    if (response.data.code === 0) {
                        insertFn(response.data.data);
                    } else {
                        throw new Error(response.data.message);
                    }
                } catch (error) {
                    ElMessage.error('图片上传失败');
                }
            }
        }
    }
};

// 数据获取相关
const categories = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const totalArticles = ref(0);
const articles = ref([]);
const filteredArticles = ref([]);
const searchForm = ref({ category: 'all', title: '' });

// 生命周期
onMounted(async () => {
    await Promise.all([
        getCategoriesInfoService().then(res => categories.value = res.data),
        getArticles()
    ]);
});

// 获取文章列表
const getArticles = async (resetPage = false) => {
    if (resetPage) currentPage.value = 1;
    try {
        const params = {
            page: currentPage.value,
            pageSize: pageSize.value,
            category: searchForm.value.category === 'all' ? null : searchForm.value.category,
            title: searchForm.value.title
        };
        const response = await articleInfoService(params);
        for (let i = 0; i < response.data.length; i++) {
            const category = await getCategoryInfoByIdService(response.data[i].categoryId);
            response.data[i].categoryName = category.data.name;
        }
        if (response && response.data) {
            articles.value = response.data;
            filteredArticles.value = response.data;
            totalArticles.value = response.total || 0;
        } else {
            ElMessage.error('获取文章数据格式有误');
        }
    } catch (error) {
        ElMessage.error('获取文章列表失败');
        console.error('获取文章列表失败:', error);
    }
};

// 处理搜索
const handleSearch = () => {
    const { category, title } = searchForm.value;
    if (category === 'all') {
        if (title) {
            filteredArticles.value = articles.value.filter((article) =>
                article.title.includes(title)
            );
        } else {
            filteredArticles.value = articles.value;
        }
    } else {
        filteredArticles.value = articles.value.filter((article) => {
            const categoryMatch = article.categoryId === Number(category);
            const titleMatch = title ? article.title.includes(title) : true;
            return categoryMatch && titleMatch;
        });
    }
};
// 打开新增文章弹窗
const openAddArticleDialog = () => {
    isAddArticleDialogVisible.value = true;
};
// 编辑器创建回调
const handleEditorCreated = (editor) => {
    editorRef.value = editor;
    // 将编辑器实例挂载到表单数据（可选）
    addArticleForm.value.editor = editor;
};
// 新增文章提交
const handleAddArticleSubmit = async () => {
    try {
        addArticleForm.value.content = editorContent.value;
        if (!addArticleForm.value.categoryId || addArticleForm.value.categoryId === 'all') {
            ElMessage.error('请选择文章分类');
            return;
        } else if (!addArticleForm.value.title) {
            ElMessage.error('请输入文章标题');
            return;
        } else if (!addArticleForm.value.articleCoverUrl) {
            ElMessage.error('请上传文章封面');
            return;
        } else if (!addArticleForm.value.content) {
            ElMessage.error('请输入文章内容');
            return;
        }
        await articleCreateService({
            ...addArticleForm.value,
            userId: userInfoStore.info.id
        });
        ElMessage.success('发布成功');
        isAddArticleDialogVisible.value = false;
        getArticles(true);
    } catch (error) {
        ElMessage.error('发布失败：' + error.message);
        console.error('发布文章失败:', error);
    }
};

// 弹窗关闭处理
const handleEditorDestroy = () => {
    nextTick(() => {
        if (editorRef.value) {
            editorRef.value.destroy();

            editorRef.value = null;
        }
    });
};

// 处理封面上传
const handleCoverUploadSuccess = (response, file, fileList) => {
    addArticleForm.value.articleCoverUrl = response.data;
    // console.log('文章封面上传成功：', response.data);
    ElMessage.success('文章封面上传成功');
};

const handleCoverUploadError = (error, file, fileList) => {
    ElMessage.error('文章封面上传失败');
    console.error('文章封面上传错误：', error);
};

// 分页处理
const handleSizeChange = (newSize) => {
    pageSize.value = newSize;
    getArticles(true);
};

const handleCurrentChange = (newPage) => {
    currentPage.value = newPage;
    getArticles();
};

// 格式化日期
const formatDate = (date) => {
    return new Date(date).toLocaleString();
};

// 跳转修改文章
const editArticle = (articleId) => {
    const url = router.resolve({ name: 'ArticleEdit', params: { id: articleId } }).href;
    window.open(url, '_blank');
};

// 删除文章
const deleteArticle = async (articleId) => {
    try {
        await articleDeleteService(articleId);
        const prevTotal = totalArticles.value;
        await getArticles();
        if (prevTotal - 1 === 0) {
            currentPage.value = 1;
        } else if (currentPage.value > Math.ceil(totalArticles.value / pageSize.value)) {
            currentPage.value = Math.max(1, Math.ceil(totalArticles.value / pageSize.value));
            getArticles();
        }
        ElMessage.success('删除文章成功');
    } catch (error) {
        ElMessage.error('删除文章失败');
        console.error('删除文章失败:', error);
    }
};
</script>

<style scoped>
/* 原有样式保持不变，新增以下样式 */
.flex {
    display: flex;
    align-items: center;
}

.article-cover {
    width: 200px;
    height: 100px;
    object-fit: cover;
    border-radius: 20px;
}

::v-deep .w-e-toolbar {
    border-bottom: 1px solid #ccc;
}

::v-deep .w-e-text-container {
    min-height: 200px;
}
</style>