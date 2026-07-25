//导入request.js请求工具
import { apiInstance } from '@/utils/apirequest.js';

//获取所有文章
export const articleInfoService = () => {
    return apiInstance.get('/article/getArticles')
}
//根据文章id获取文章
export const articleDetailService = (articleId) => {
    return apiInstance.get(`/article/getArticleById/${articleId}`)
}
//获取文章评论
export const articleCommentService = (articleId) => {
    return apiInstance.get(`/article/${articleId}/comments`)
}
//创建文章
export const articleCreateService = (data) => {
    return apiInstance.post('/article/addArticle', data)
}
//创建评论
export const createCommentService = (comment) => {
    return apiInstance.post('/article/addcomments', comment)
}
//删除评论
export const deleteCommentService = (comment) => {
    return apiInstance.post('/article/deletecomments', comment)
}
//点赞
export const addLikeService = (articleId) => {
    return apiInstance.post(`/article/${articleId}/like`, articleId)
}
//取消点赞
export const cancelLikeService = (articleId) => {
    return apiInstance.delete(`/article/${articleId}/cancellike`, articleId)
}
//判断是否已点赞
export const isLikeService = (articleId) => {
    return apiInstance.get(`/article/${articleId}/iflike`)
}
//判断是否已收藏
export const isCollectService = (articleId) => {
    return apiInstance.get(`/article/${articleId}/ifcollect`)
}
//收藏
export const collectService = (articleId) => {
    return apiInstance.post(`/article/${articleId}/collect`, articleId)
}
//取消收藏
export const cancelCollectService = (articleId) => {
    return apiInstance.delete(`/article/${articleId}/cancelcollect`, articleId)
}
//增加阅读量
export const addReadService = (articleId) => {
    return apiInstance.get(`/article/increaseViews/${articleId}`, articleId)
}
//获取最佳作者
export const getHotAuthorService = () => {
    return apiInstance.get('/article/getHotAuthor')
}
//根据作者id获取文章
export const getArticleByAuthorIdService = (userId) => {
    return apiInstance.get(`/article/${userId}`)
}
//根据用户id查找用户喜欢的文章
export const getLikeArticleByUserIdService = (userId) => {
    return apiInstance.get(`/article/getLikeArticleByUserId/${userId}`)
}
//根据用户id查找用户收藏的文章
export const getCollectArticleByUserIdService = (userId) => {
    return apiInstance.get(`/article/getCollectArticleByUserId/${userId}`)
}
//编辑文章
export const editArticleService = (data) => {
    return apiInstance.post('/article/editArticle', data)
}
//删除文章
export const articleDeleteService = (articleId) => {
    return apiInstance.delete(`/article/deleteArticle/${articleId}`)
}
//编辑文章
export const articleUpdateService = (data) => {
    return apiInstance.put('/article/updateArticle', data)
}
//获取全部评论
export const getAllCommentsService = () => {
    return apiInstance.get('/article/getAllComments')
}
//获取文章全部点赞
export const getAllLikesService = () => {
    return apiInstance.get('/article/getAllLikes')
}
//根据喜欢id取消喜欢
export const cancelLikeByIdService = (likeId) => {
    return apiInstance.delete(`/article/cancelLikeById/${likeId}`)
}
//获取全部收藏
export const getAllCollectsService = () => {
    return apiInstance.get('/article/getAllCollects')
}
//根据收藏id取消收藏
export const cancelCollectByIdService = (collectId) => {
    return apiInstance.delete(`/article/cancelCollectById/${collectId}`)
}