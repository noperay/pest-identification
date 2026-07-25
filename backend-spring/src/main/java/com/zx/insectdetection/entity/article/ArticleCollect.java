package com.zx.insectdetection.entity.article;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "article_collect")
public class ArticleCollect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer articleId;
    private Date createTime;
}
