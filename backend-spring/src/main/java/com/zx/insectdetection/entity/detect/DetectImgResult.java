package com.zx.insectdetection.entity.detect;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DetectImgResult {
    @NotNull
    private Integer id;//主键ID
    @NotNull
    private Integer imgId;
    private String sort;
    private String confidenceLevel;
}
