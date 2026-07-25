<template>
    <div class="hotArticleContainer" v-for="article in filteredArticles" :key="article.id">
        <div class="articleTitle" @click="goToArticleDetail(article.id)">
            {{ article.title }}
            <el-icon class="viewIcon">
                <View />
            </el-icon>
            <div class="viewText"> {{ article.views }}</div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {
    articleInfoService,
    addReadService
} from '@/api/article.js';
import { ElMessage } from 'element-plus';
import useUserInfoStore from '@/stores/userInfo.js';

const router = useRouter();
const articles = ref([]);

const getUserArticles = async () => {
    try {
        const res = await articleInfoService();
        articles.value = res.data;
    } catch (error) {
        ElMessage.error('获取文章列表失败');
        console.error('获取文章列表出错:', error);
    }
};
const goToArticleDetail = async (articleId) => {
    //文章view+1
    let result = await addReadService(articleId);
    getUserArticles();
    const url = router.resolve({ name: 'ArticleDetail', params: { id: articleId } }).href;
    window.open(url, '_blank');
};
onMounted(() => {
    getUserArticles();
});

const filteredArticles = computed(() => {
    return articles.value
        .filter(article => article.views > 1000)
        .sort((a, b) => b.views - a.views)
        .slice(0, 10);
});
</script>

<style scoped>
.hotArticleContainer {
    margin-left: 6px;
    align-items: center;
    display: flex;
    font-size: 17px;
}

.hotArticleContainer:hover {
    color: rgb(46, 110, 252);
    cursor: pointer;
}

.articleTitle {
    display: flex;
    width: 100%;
    margin-top: 7px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.viewIcon {
    margin-left: 10px;
    color: #c5c5c5;
    margin-top: 3px;
    font-size: 15px;
}

.viewText {
    margin-top: 2px;
    margin-left: 5px;
    color: #919191;
    font-size: 14px;
}
</style>