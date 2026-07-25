package com.zx.insectdetection.service;

import com.zx.insectdetection.entity.article.Article;
import com.zx.insectdetection.entity.article.ArticleComment;
import com.zx.insectdetection.entity.user.User;

import java.util.List;

public interface ArticleService{
    List<Article> findAllArticles();

    List<Article> findByUserId(Integer userId);

    Boolean addArticle(Article article);

    Boolean updateArticle(Integer articleId, Article article);

    Boolean deleteArticle(Integer articleId);

    Boolean addArticleView(Integer articleId);

    String addComment(ArticleComment comment);

    List<ArticleComment> getArticleComments(Integer articleId);

    Boolean deleteComment(ArticleComment comment);

    Boolean addArticleCollect(Integer articleId);

    Boolean delArticleCollect(Integer articleId);

    Boolean addArticleLike(Integer articleId);

    Boolean delArticleLike(Integer articleId);

    Article findArticleById(Integer articleId);

    Boolean ifArticleLike(Integer articleId);

    Boolean ifArticleCollect(Integer articleId);


}
