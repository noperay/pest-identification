package com.zx.insectdetection.entity.detect;

import com.zx.insectdetection.entity.others.ResultObject;
import lombok.Data;

import java.util.List;

@Data
public class DetectImgUrl {
    private String imgUrl;
    private List<ResultObject> result;
}

