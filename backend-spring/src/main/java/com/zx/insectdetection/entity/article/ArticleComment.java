package com.zx.insectdetection.entity.article;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "article_comment")
public class ArticleComment {
    @Id
    private Integer id;
    private String content;
    private Integer userId;
    private Integer articleId;
    @CreationTimestamp
    private Date createTime;
    @Column(name = "like_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer likeCount;

}