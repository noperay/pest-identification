<template>
    <el-card class="UserprofileContainer">
        <div class="navandsearch" style="align-items: center; color: dimgrey;display: flex;">
            <div class="nav">
                <div v-for="(nav, index) in navList" :key="index"
                    :class="{ 'nav-item': true, 'active': currentNavIndex === index }" @click="handleNavClick(index)">
                    {{ nav }}
                </div>
            </div>
        </div>
        <div v-if="currentNavIndex === 0">
            <div v-for="article in articlelist" :key="article.id" class="article-item">
                <img :src="article.articleCoverUrl || avatar" alt="文章封面" class="article-cover">
                <div class="article-info">
                    <h3 class="article-title" @click="goToArticleDetail(article.id)">{{ article.title }}</h3>
                    <p class="article-content">{{ stripTags(article.content).slice(0, 50) }}...</p>
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
            </div>
        </div>
        <div v-if="currentNavIndex === 1">
            <div v-for="article in likelist" :key="article.id" class="article-item">
                <img :src="article.articleCoverUrl || avatar" alt="文章封面" class="article-cover">
                <div class="article-info">
                    <h3 class="article-title" @click="goToArticleDetail(article.id)">{{ article.title }}</h3>
                    <p class="article-content">{{ stripTags(article.content).slice(0, 50) }}...</p>
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
            </div>
        </div>
        <div v-if="currentNavIndex === 2">
            <div v-for="article in collectlist" :key="article.id" class="article-item">
                <img :src="article.articleCoverUrl || avatar" alt="文章封面" class="article-cover">
                <div class="article-info">
                    <h3 class="article-title" @click="goToArticleDetail(article.id)">{{ article.title }}</h3>
                    <p class="article-content">{{ stripTags(article.content).slice(0, 50) }}...</p>
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
            </div>
        </div>
    </el-card>
</template>
<script setup>
import { ElMessage, ElInput, ElButton } from 'element-plus';
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getArticleByAuthorIdService } from '@/api/article.js';
import {
    addLikeService,
    isLikeService,
    isCollectService,
    collectService,
    cancelLikeService,
    cancelCollectService,
    addReadService,
    getLikeArticleByUserIdService,
    getCollectArticleByUserIdService

} from '@/api/article.js';
import avatar from '@/assets/default.png';

const route = useRoute();
const router = useRouter();
const searchKeyword = ref('');
const currentNavIndex = ref(0);
const userId = route.params.userId;
const navList = ['文章', '点赞', '收藏'];
const articlelist = ref([]);
const likelist = ref([]);
const collectlist = ref([]);

//查找用户发布文章
const getArticleInfo = async () => {
    let result = await getArticleByAuthorIdService(userId);
    articlelist.value = result.data.reverse();
}

//查找用户点赞的文章
const getLikeArticleInfo = async () => {
    let result = await getLikeArticleByUserIdService(userId);
    likelist.value = result.data.reverse();
}

//查找用户收藏的文章
const getCollectArticleInfo = async () => {
    let result = await getCollectArticleByUserIdService(userId);
    collectlist.value = result.data.reverse();
}

// 跳转到文章详情页
const goToArticleDetail = async (articleId) => {
    await addReadService(articleId);
    getArticleInfo();
    const url = router.resolve({ name: 'ArticleDetail', params: { id: articleId } }).href;
    window.open(url, '_blank');
};

//点赞
const addLike = async (articleId) => {
    //判断是否已点赞
    const isLiked = await isLikeService(articleId);
    if (isLiked.data === "已点赞") {
        await cancelLikeService(articleId);
        getArticleInfo();
        getLikeArticleInfo();
        ElMessage.success('取消点赞成功');
        return;
    } else {
        try {
            await addLikeService(articleId);
            getArticleInfo();
            getLikeArticleInfo();
            ElMessage.success('点赞成功');
        } catch (error) {
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
        getCollectArticleInfo();
        ElMessage.success('取消收藏成功');
    } else {
        try {
            await collectService(articleId);
            getArticleInfo();
            getCollectArticleInfo();
            ElMessage.success('收藏成功');
        } catch (error) {
            ElMessage.error('收藏失败');
        }
    }
}

// 处理导航点击事件
const handleNavClick = (index) => {
    currentNavIndex.value = index;
    if (index === 0) {
        getArticleInfo();
    } else if (index === 1) {
        getLikeArticleInfo();
    } else if (index === 2) {
        getCollectArticleInfo();
    }
}

// 处理搜索事件
const handleSearch = () => {
    // 这里可以添加搜索逻辑
    console.log('搜索关键词:', searchKeyword.value);
}

// 去除 HTML 标签
const stripTags = (str) => {
    // return str.replace(/<[^>]*>/g, '');
    return str
        .replace(/<[^>]*>/g, '')    // 去除 HTML 标签 
        .replace(/&nbsp;/g, '')     // 去除 &nbsp;
        .replace(/\s+/g, ' ')       // 合并连续空格 
        .trim();                    // 去除首尾空格 
    // return text.replace(/<[^>]*>/g, '');
}

onMounted(() => {
    getArticleInfo();
    getLikeArticleInfo();
    getCollectArticleInfo();
});
</script>
<style scoped>
::v-deep .el-card__body {
    padding: 0px;
}

.UserprofileContainer {
    /* border: 1px solid red; */
    /* width: 100%; */
    width: 810px;
    height: 100%;
}

.navandsearch {
    /* border: 1px solid red; */
    border-bottom: solid 1px #ccc;
}

.nav {
    margin-left: 20px;
    margin-top: 15px;
    margin-bottom: 15px;
    display: flex;
    /* border: 1px solid red; */
}

.nav-item {
    margin-right: 20px;
    cursor: pointer;
    padding-bottom: 2px;
    border-bottom: 2px solid transparent;
    transition: border-bottom-color 0.3s;
}

.active {
    /* border: 1px solid red; */
    border-bottom-color: black;
}

.search {
    margin-top: 15px;
    margin-bottom: 15px;
}

.article-item {
    /* border: 1px solid red; */
    /* width: 100%; */
    width: 810px;
    display: flex;
    align-items: center;
    padding: 10px;
    border-bottom: 1px solid #ccc;
}

.article-cover {
    width: 120px;
    height: 80px;
    object-fit: cover;
    margin-right: 10px;
    border: 1px solid #ccc;
    border-radius: 20px;
}

.article-info {
    /* flex: 1; */
    width: 810px;
}

.article-title {
    margin: 0;
    font-size: 16px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.article-title:hover {
    color: rgb(46, 110, 252);
    cursor: pointer;
}

.article-content {
    margin: 0;
    font-size: 14px;
    color: #919191;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    width: 660px;
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