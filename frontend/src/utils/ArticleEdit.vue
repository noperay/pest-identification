<template>
    <el-card class="article-edit-container">
        <template #header>
            <div class="clearfix">
                <span>编辑文章</span>
            </div>
        </template>
        <el-form :model="editArticleForm" ref="editArticleFormRef" label-width="80px">
            <el-form-item label="文章分类">
                <el-select v-model="editArticleForm.categoryId" placeholder="请选择文章分类">
                    <el-option key="all" label="不限" value="all" />
                    <el-option v-for="category in categories" :key="category.id" :label="category.name"
                        :value="category.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="文章标题">
                <el-input v-model="editArticleForm.title" placeholder="请输入文章标题" />
            </el-form-item>
            <el-form-item label="文章封面">
                <el-upload ref="uploadRef" class="article-cover-uploader" :show-file-list="false" :auto-upload="true"
                    action="/api/file/upload" name="file" :headers="{ 'Authorization': tokenStore.token }"
                    :on-success="handleCoverUploadSuccess" :on-error="handleCoverUploadError">
                    <img :src="editArticleForm.articlePic ? editArticleForm.articlePic : cover" class="article-cover"
                        width="200px" />
                </el-upload>
            </el-form-item>
            <el-form-item label="文章内容">
                <div style="border: 1px solid #ccc">
                    <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" style="border-bottom: 1px solid #ccc" />
                    <Editor v-model="editorContent" :defaultConfig="editorConfig"
                        style="height: 300px; overflow-y: hidden;" @onCreated="handleEditorCreated" />
                </div>
            </el-form-item>
        </el-form>
        <div class="button-group">
            <el-button type="primary" @click="handleEditArticleSubmit">保存</el-button>
        </div>
    </el-card>
</template>

<script setup>
import { ref, onMounted, nextTick, onBeforeUnmount, shallowRef } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getCategoriesInfoService } from '@/api/category.js';
import { articleDetailService, articleUpdateService } from '@/api/article.js';
import { useTokenStore } from '@/stores/token.js';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import { ElMessage } from 'element-plus';
import cover from '@/assets/cover.jpg';

const tokenStore = useTokenStore();
const route = useRoute();
const router = useRouter();

// 编辑文章相关
const editArticleForm = ref({
    categoryId: null,
    title: '',
    content: '',
    articlePic: ''
});
const editArticleFormRef = ref(null);
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
                    const response = await articleUpdateService(formData);
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

// 分类数据
const categories = ref([]);

// 获取分类信息
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

// 获取文章详情
const getArticleInfoById = async () => {
    const articleId = route.params.id;
    try {
        const result = await articleDetailService(articleId);
        editArticleForm.value = {
            categoryId: result.data.categoryId,
            title: result.data.title,
            articlePic: result.data.articleCoverUrl,
            content: result.data.content
        };
        editorContent.value = result.data.content;
    } catch (error) {
        ElMessage.error('获取文章详情失败');
        console.error('获取文章详情失败:', error);
    }
};

// 处理封面上传
const handleCoverUploadSuccess = (response, file, fileList) => {
    editArticleForm.value.articlePic = response.data;
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
    editArticleForm.value.editor = editor;
};

// 摧毁富文本编辑器
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

// 表单提交处理
const handleEditArticleSubmit = async () => {
    try {
        // 获取富文本内容
        editArticleForm.value.content = editorContent.value;
        if (!editArticleForm.value.categoryId || editArticleForm.value.categoryId === 'all') {
            ElMessage.error('请选择文章分类');
            return;
        } else if (!editArticleForm.value.title) {
            ElMessage.error('请输入文章标题');
            return;
        } else if (!editArticleForm.value.articlePic) {
            ElMessage.error('请上传文章封面');
            return;
        } else if (!editArticleForm.value.content) {
            ElMessage.error('请输入文章内容');
            return;
        }
        // 处理分类id
        let categoryId = editArticleForm.value.categoryId;
        if (typeof categoryId === 'string' && categoryId === 'all') {
            categoryId = null;
        } else {
            categoryId = Number(categoryId);
        }

        const articleId = route.params.id;
        // 提交逻辑（根据实际接口修改）
        await articleUpdateService({
            id: articleId,
            title: editArticleForm.value.title,
            content: editArticleForm.value.content,
            categoryId: categoryId,
            articleCoverUrl: editArticleForm.value.articlePic
        });
        ElMessage.success(' 文章更新成功');
        router.back();
    } catch (error) {
        ElMessage.error(' 文章更新失败：' + error.message);
        console.error(' 文章更新失败:', error);
    }
};


// 生命周期
onMounted(async () => {
    await Promise.all([
        getCategoriesInfo(),
        getArticleInfoById()
    ]);
});

onBeforeUnmount(() => {
    handleEditorDestroy();
});
</script>

<style scoped>
@import '../../node_modules/@wangeditor/editor/dist/css/style.css';

.article-edit-container {
    height: 100%;
    margin: auto;
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

.button-group {
    margin-top: 20px;
    text-align: right;
}
</style>