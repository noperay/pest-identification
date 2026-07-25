package com.zx.insectdetection.service.Impl;

import com.zx.insectdetection.entity.user.User;
import com.zx.insectdetection.mapper.ArticleMapper;
import com.zx.insectdetection.entity.article.Article;
import com.zx.insectdetection.entity.article.ArticleComment;
import com.zx.insectdetection.mapper.UserMapper;
import com.zx.insectdetection.service.ArticleService;
import com.zx.insectdetection.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserMapper userMapper;
    @Override
    public List<Article> findAllArticles() {
        List<Article> articles = articleMapper.findAllArticles();
        if (articles == null) {
            return Collections.emptyList();
        }
        return articles;
    }

    @Override
    public List<Article> findByUserId(Integer userId) {
        List<Article> articles = articleMapper.findByUserId(userId);
        if (articles == null) {
            return Collections.emptyList();
        }
        return articles;
    }

    @Override
    public Boolean addArticle(Article article) {
        article.setStatus("已发布");
        article.setViews(0);
        article.setCollectCount(0);
        article.setCommentCount(0);
        article.setLikeCount(0);
        return articleMapper.addArticle(article);
    }

    @Override
    public Boolean updateArticle(Integer articleId, Article article) {
        article.setId(articleId);
        article.setStatus("已发布");
        return articleMapper.updateArticle(article);
    }

    @Override
    public Boolean deleteArticle(Integer articleId) {
        return articleMapper.deleteArticle(articleId);
    }

    @Override
    public Boolean addArticleView(Integer articleId) {
        Article article = articleMapper.findArticleById(articleId);
        userMapper.addViewsCount(article.getUserId());
        return articleMapper.addArticleView(articleId);
    }

    @Override
    public String addComment(ArticleComment comment) {
        Map<String,Object> map = ThreadLocalUtil.get();
        comment.setUserId((Integer) map.get("id"));
        boolean result = articleMapper.addCommentCount(comment.getArticleId());
        boolean result1 = articleMapper.addComment(comment);
        if (!result) {
            return "count+1失败";
        }else if(!result1){
            return "评论失败";
        }
        return "评论成功";
    }

    @Override
    public List<ArticleComment> getArticleComments(Integer articleId) {
        return articleMapper.getArticleComments(articleId);
    }

    @Override
    public Boolean deleteComment(ArticleComment comment) {
        boolean result = articleMapper.delCommentCount(comment.getArticleId());
        boolean result1 = articleMapper.deleteComment(comment.getId());
        if(result && result1){
            return true;
        }
        return false;
    }

    @Override
    public Boolean addArticleCollect(Integer articleId) { //收藏
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        if (articleMapper.ifCollect(userId,articleId) >= 1){
            return false;
        }
        boolean result = articleMapper.addCollectCount(articleId);
        boolean result1 = articleMapper.addArticleCollect(userId,articleId);
        if(result && result1){
            return true;
        }
        return false;
    }

    @Override
    public Boolean delArticleCollect(Integer articleId) { //取消收藏
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        if (articleMapper.ifCollect(userId,articleId) >= 1){
            boolean result = articleMapper.delCollectCount(articleId);
            boolean result1 = articleMapper.delArticleCollect(userId,articleId);
            if(result && result1){
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean addArticleLike(Integer articleId) { //文章点赞
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        boolean result = articleMapper.addArticleLike(userId,articleId);
        if(result){
            boolean result1 = articleMapper.addLikeCount(articleId);
            if(result1){
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean delArticleLike(Integer articleId) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        boolean result = articleMapper.delArticleLike(userId,articleId);
        if(result){
            boolean result1 = articleMapper.delLikeCount(articleId);
            if(result1){
                return true;
            }
        }
        return false;
    }

    @Override
    public Article findArticleById(Integer articleId) {
        return articleMapper.findArticleById(articleId);
    }

    @Override
    public Boolean ifArticleLike(Integer articleId) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        if (articleMapper.ifArticleLike(userId,articleId) >= 1){
            return true;
        }
        return false;
    }

    @Override
    public Boolean ifArticleCollect(Integer articleId) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        if (articleMapper.ifArticleCollect(userId,articleId) >= 1){
            return true;
        }
        return false;
    }


}
