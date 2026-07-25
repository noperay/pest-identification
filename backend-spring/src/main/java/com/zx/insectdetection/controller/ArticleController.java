package com.zx.insectdetection.controller;

import com.zx.insectdetection.entity.article.Article;
import com.zx.insectdetection.entity.article.ArticleCollect;
import com.zx.insectdetection.entity.article.ArticleComment;
import com.zx.insectdetection.entity.article.ArticleFavorite;
import com.zx.insectdetection.entity.others.Result;
import com.zx.insectdetection.entity.user.User;
import com.zx.insectdetection.mapper.ArticleMapper;
import com.zx.insectdetection.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
@Validated
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleMapper articleMapper;

    // 获取所有文章
    @GetMapping("/getArticles")
    public Result<List<Article>> getAllArticles() {
        List<Article> articles = articleService.findAllArticles();
        return Result.success(articles);
    }

    //根据文章id获取文章
    @GetMapping("/getArticleById/{articleId}")
    public Result<Article> getArticleByArticleId(@PathVariable Integer articleId) {
        Article article = articleService.findArticleById(articleId);
        return Result.success(article);
    }

    // 根据某User文章
    @GetMapping("/{userId}")
    public Result<List<Article>> getArticleById(@PathVariable Integer userId) {
        List<Article> articles = articleService.findByUserId(userId);
        return Result.success(articles);
    }

    // 创建文章
    @PostMapping("/addArticle")
    public Result<String> createArticle(@RequestBody Article article) {
        Boolean result = articleService.addArticle(article);
        if (result) {
            return Result.success("创建成功");
        }
        return Result.error("创建失败");
    }

    // 更新文章
    @PutMapping("/{articleId}")
    public Result<String> updateArticle(@PathVariable Integer articleId, @RequestBody Article article) {
        Boolean result = articleService.updateArticle(articleId, article);
        if (result) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    // 删除文章
    @DeleteMapping("/deleteArticle/{articleId}")
    public Result<String> deleteArticle(@PathVariable Integer articleId) {
        Boolean result = articleService.deleteArticle(articleId);
        if (result) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    // 文章浏览量
    @GetMapping("/increaseViews/{articleId}")
    public Result<String> addArticleView(@PathVariable Integer articleId) {
        Boolean result = articleService.addArticleView(articleId);
        if (result) {
            return Result.success("增加成功");
        }
        return Result.error("增加失败");
    }


    // 添加评论
    @PostMapping("/addcomments")
    public Result<String> addComment(@RequestBody ArticleComment comment) {
        String result = articleService.addComment(comment);
        if( result.equals("评论成功")){
            return Result.success();
        }else{
            return Result.error(result);
        }
    }

    // 获取文章的所有评论
    @GetMapping("/{articleId}/comments")
    public Result<List<ArticleComment>> getArticleComments(@PathVariable Integer articleId) {
        List<ArticleComment> comments = articleService.getArticleComments(articleId);
        return Result.success(comments);
    }

    // 删除评论
    @PostMapping("/deletecomments")
    public Result<String> deleteComment(@RequestBody ArticleComment comment) {
        articleService.deleteComment(comment);
        return Result.success("删除成功");
    }

    // 添加收藏
    @PostMapping("/{articleId}/collect")
    public Result<String> addArticleCollect(@PathVariable Integer articleId) {
        Boolean result = articleService.addArticleCollect(articleId);
        if (result) {
            return Result.success("收藏成功");
        }
        return Result.error("收藏失败");
    }
    //判断是否已收藏
    @GetMapping("/{articleId}/ifcollect")
    public Result<String> addArticleIfCollect(@PathVariable Integer articleId) {
        Boolean result = articleService.ifArticleCollect(articleId);
        if (result) {
            return Result.success("已收藏");
        }
        return Result.success("未收藏");
    }

    // 取消收藏
    @DeleteMapping("/{articleId}/cancelcollect")
    public Result<String> delArticleCollect(@PathVariable Integer articleId) {
        Boolean result = articleService.delArticleCollect(articleId);
        if (result) {
            return Result.success("取消成功");
        }
        return Result.error("取消失败");
    }
    // 点赞文章
    @PostMapping("/{articleId}/like")
    public Result<String> addArticleLike(@PathVariable Integer articleId) {
        Boolean result = articleService.addArticleLike(articleId);
        if (result) {
            return Result.success("点赞成功");
        }
        return Result.error("点赞失败");
    }
    //判断是否已点赞
    @GetMapping("/{articleId}/iflike")
    public Result<String> ifArticleLike(@PathVariable Integer articleId) {
        Boolean result = articleService.ifArticleLike(articleId);
        if (result) {
            return Result.success("已点赞");
        }
        return Result.success("未点赞");
    }

    // 取消点赞
    @DeleteMapping("/{articleId}/cancellike")
    public Result<String> delArticleLike(@PathVariable Integer articleId) {
        Boolean result = articleService.delArticleLike(articleId);
        if (result) {
            return Result.success("取消成功");
        }
        return Result.error("取消失败");
    }
    //根据用户id查找用户收藏的文章
    @GetMapping("/getCollectArticleByUserId/{userId}")
    public Result<List<Article>> getCollecArticleByUserId(@PathVariable Integer userId) {
        List<Article> articles = articleMapper.getCollecArticleByUserId(userId);
        return Result.success(articles);
    }
    //根据用户id查找用户喜欢的文章
    @GetMapping("/getLikeArticleByUserId/{userId}")
    public Result<List<Article>> getLikeArticleByUserId(@PathVariable Integer userId) {
        List<Article> articles = articleMapper.getLikeArticleByUserId(userId);
        return Result.success(articles);
    }
    //更新文章
    @PutMapping("/updateArticle")
    public Result<String> updateArticle(@RequestBody Article article) {
        System.out.println(article);
        Boolean result = articleService.updateArticle(article.getId(), article);
        if (result) {
            return Result.success();
        }
        return Result.error("更新失败");
    }
    //获取网站全部评论
    @GetMapping("/getAllComments")
    public Result<List<ArticleComment>> getAllComments() {
        List<ArticleComment> comments = articleMapper.getAllComments();
        return Result.success(comments);

    }
    //获取全部点赞
    @GetMapping("/getAllLikes")
    public Result<List<ArticleFavorite>> getAllLikes() {
        List<ArticleFavorite> articleFavorites = articleMapper.getAllLikes();
        return Result.success(articleFavorites);
    }
    //根据likeId取消点赞
    @DeleteMapping("/cancelLikeById/{likeId}")
    public Result<String> cancelLikeById(@PathVariable Integer likeId) {
        articleMapper.cancelLikeById(likeId);
        return Result.success("取消成功");
    }
    //获取全部收藏列表
    @GetMapping("/getAllCollects")
    public Result<List<ArticleCollect>> getAllCollects() {
        List<ArticleCollect> articles = articleMapper.getAllCollects();
        return Result.success(articles);
    }
    //根据收藏id取消收藏
    @DeleteMapping("/cancelCollectById/{collectId}")
    public Result<String> cancelCollectById(@PathVariable Integer collectId) {
        articleMapper.cancelCollectById(collectId);
        return Result.success("取消成功");
    }
    //计算所有文章数量
//    @GetMapping("/getAllArticlesCount")
//    public Result<List<Article>> getAllArticlesCount() {
//        List<Article> articles = articleMapper.getAllArticlesCount();
//        return Result.success(articles);
//    }


}