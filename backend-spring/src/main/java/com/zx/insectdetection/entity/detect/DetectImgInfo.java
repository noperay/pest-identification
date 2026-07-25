package com.zx.insectdetection.entity.detect;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Data
@Table(name = "detect_detail")
public class DetectImgInfo {
    @NotNull
    @Column(name = "id")
    private Integer id;//主键ID
    @NotNull
    private Integer userId;
    private String imgUrl;
    private String sort;
    private String confidenceLevel;
    private LocalDateTime createTime;//创建时间
}

