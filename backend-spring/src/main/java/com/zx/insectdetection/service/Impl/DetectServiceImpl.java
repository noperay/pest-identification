package com.zx.insectdetection.service.Impl;

import com.zx.insectdetection.mapper.DetectMapper;
import com.zx.insectdetection.entity.detect.DetectImgInfo;
import com.zx.insectdetection.service.DetectService;
import com.zx.insectdetection.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DetectServiceImpl implements DetectService {
    @Autowired
    private DetectMapper detectMapper;

    @Override
    public int addImgUrl(String imgUrl) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        detectMapper.addImgUrl(imgUrl, id);
        Integer imgId = detectMapper.selectLastId();
//        System.out.println(imgId);
        return imgId;
//        detectMapper.addDetectResult(imgId,)
    }

    @Override
    public List<DetectImgInfo> getDetectHistory(Integer id) {
        List<DetectImgInfo> detectImgInfo = detectMapper.getDetectHistory(id);
        return detectImgInfo;
    }


    @Override
    public void insertDetectResult(String sort, double confidenceLevel, Integer imgId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        // 将置信度保留三位小数
        double formattedConfidence = Math.round(confidenceLevel * 1000) / 1000.0;
        detectMapper.insertDetectResult(imgId, sort, formattedConfidence, userId);
    }

    @Override
    public List<DetectImgInfo> getUserDetectList(Integer userId) {
        return detectMapper.getUserDetectList(userId);
    }

    @Override
    public void deleteDetectRecord(Integer imgId) {
        detectMapper.deleteDetectRecord(imgId);
    }
}