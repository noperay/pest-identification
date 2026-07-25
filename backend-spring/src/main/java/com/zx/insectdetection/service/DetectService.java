package com.zx.insectdetection.service;

import com.zx.insectdetection.entity.detect.DetectImgInfo;
import org.hibernate.validator.constraints.URL;

import java.util.List;


public interface DetectService {
    int addImgUrl(@URL String imgUrl);

     List<DetectImgInfo> getDetectHistory(Integer id);


    void insertDetectResult(String sort, double confidenceLevel,Integer imgId);

    List<DetectImgInfo> getUserDetectList(Integer userId);

    void deleteDetectRecord(Integer imgId);
}
