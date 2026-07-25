package com.zx.insectdetection.entity.article;


import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
@Data
@Entity
@Table(name = "article")
public class Article {
    @Id
    @NotNull
    private Integer id;

    private String title;
    private String content;

    @NotNull
    private Integer categoryId;
    private Integer userId;

    private Date createTime;
    private Date updateTime;
    private String status;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int views;
    private String articleCoverUrl;
    @Column(columnDefinition = "INT DEFAULT 0")
    private int likeCount;
    @Column(columnDefinition = "INT DEFAULT 0")
    private int commentCount;
    @Column(columnDefinition = "INT DEFAULT 0")
    private int collectCount;

}