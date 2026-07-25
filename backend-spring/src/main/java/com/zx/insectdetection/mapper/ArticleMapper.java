package com.zx.insectdetection.mapper;

import com.zx.insectdetection.entity.article.Article;
import com.zx.insectdetection.entity.article.ArticleCollect;
import com.zx.insectdetection.entity.article.ArticleComment;
import com.zx.insectdetection.entity.article.ArticleFavorite;
import com.zx.insectdetection.entity.user.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface ArticleMapper {
    //查找所有文章
    @Select("select * from article")
    List<Article> findAllArticles();
    //根据用户查找文章
    @Select("select * from article where user_id = #{userId}")
    List<Article> findByUserId(Integer userId);
    //添加文章
    @Insert("insert into article (title, content, category_id, user_id, create_time, update_time, status, views, article_cover_url,like_count,collect_count,comment_count) " +
            "values (#{title}, #{content}, #{categoryId}, #{userId}, now(), now(), #{status}, #{views}, #{articleCoverUrl},#{likeCount},#{collectCount},#{commentCount})")
    Boolean addArticle(Article article);
    //更新文章
    @Update("update article set title = #{title}, content = #{content}, category_id = #{categoryId}, update_time=now(),status=#{status},views=#{views},article_cover_url=#{articleCoverUrl} where id = #{id} ")
    Boolean updateArticle(Article article);
    //根据文章id 删除文章
    @Delete("delete from article where id = #{articleId}")
    Boolean deleteArticle(Integer articleId);
    //根据文章id增加浏览量
    @Update("update article set views = views + 1 where id = #{articleId}")
    Boolean addArticleView(Integer articleId);
    //增加评论
    @Insert("insert into article_comment (user_id, article_id, content, create_time) " +
            "values (#{userId}, #{articleId}, #{content}, now())")
    Boolean addComment(ArticleComment comment);
    //增加评论量+1
    @Update("update article set comment_count = comment_count + 1 where id = #{articleId}")
    Boolean addCommentCount(Integer articleId);
    //减少评论量-1
    @Update("update article set comment_count = comment_count - 1 where id = #{articleId}")
    Boolean delCommentCount(Integer articleId);
    //查找所有评论
    @Select("select * from article_comment where article_id = #{articleId}")
    List<ArticleComment> getArticleComments(Integer articleId);
    //根据id删除评论
    @Delete("delete from article_comment where id = #{commentId}")
    Boolean deleteComment(Integer commentId);
    //判断是否收藏
    @Select("select count(*) from article_collect where user_id = #{userId} and article_id = #{articleId}")
    int ifCollect(@Param("userId")Integer userId,@Param("articleId")  Integer articleId);
    //收藏
    @Update("insert into article_collect (user_id, article_id,create_time) values (#{userId}, #{articleId},now())")
    Boolean addArticleCollect(@Param("userId") Integer userId,@Param("articleId") Integer articleId);
    //收藏count+1
    @Update("update article set collect_count = collect_count + 1 where id = #{articleId}")
    Boolean addCollectCount(Integer articleId);
    //收藏count-1
    @Update("update article set collect_count = collect_count - 1 where id = #{articleId}")
    Boolean delCollectCount(Integer articleId);
    //取消收藏
    @Delete("delete from article_collect where user_id = #{userId} and article_id = #{articleId}")
    Boolean delArticleCollect(@Param("userId") Integer userId,@Param("articleId") Integer articleId);
    //点赞
    @Insert("insert into article_like (user_id, article_id,create_time) values (#{userId}, #{articleId},now())")
    boolean addArticleLike(@Param("userId")Integer userId,@Param("articleId") Integer articleId);
    //取消点赞
    @Delete("delete from article_like where user_id = #{userId} and article_id = #{articleId}")
    boolean delArticleLike(@Param("userId")Integer userId,@Param("articleId") Integer articleId);
    //点赞count+
    @Update("update article set like_count = like_count + 1 where id = #{articleId}")
    Boolean addLikeCount(Integer articleId);
    //取消点赞count-
    @Update("update article set like_count = like_count - 1 where id = #{articleId}")
    Boolean delLikeCount(Integer articleId);

    //根据id查找文章
    @Select("select * from article where id = #{articleId}")
    Article findArticleById(Integer articleId);

    //判断是否点赞
    @Select("select count(*) from article_like where user_id = #{userId} and article_id = #{articleId}")
    int ifArticleLike(@Param("userId")Integer userId,@Param("articleId") Integer articleId);

    //判断是否收藏
    @Select("select count(*) from article_collect where user_id = #{userId} and article_id = #{articleId}")
    int ifArticleCollect(@Param("userId")Integer userId,@Param("articleId") Integer articleId);

    //根据用户id查找喜欢的文章
    @Select("select * from article where id in (select article_id from article_like where user_id = #{userId})")
    List<Article> getLikeArticleByUserId(Integer userId);

    //根据用户id查找收藏的文章
    @Select("select * from article where id in (select article_id from article_collect where user_id = #{userId})")
    List<Article> getCollecArticleByUserId(Integer userId);

    //查找所有评论
    @Select("select * from article_comment")
    List<ArticleComment> getAllComments();

    //获取全部点赞
    @Select("select * from article_like")
    List<ArticleFavorite> getAllLikes();

    //取消点赞
    @Delete("delete from article_like where id = #{likeId}")
    void cancelLikeById(Integer likeId);

    //获取全部收藏
    @Select("select * from article_collect")
    List<ArticleCollect> getAllCollects();

    //取消收藏
    @Delete("delete from article_collect where id = #{collectId}")
    void cancelCollectById(Integer collectId);
}
