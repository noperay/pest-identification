package com.zx.insectdetection.controller;

import com.zx.insectdetection.entity.detect.DetectImgInfo;
import com.zx.insectdetection.entity.detect.DetectImgUrl;
import com.zx.insectdetection.entity.others.Result;
import com.zx.insectdetection.entity.others.ResultObject;
import com.zx.insectdetection.mapper.DetectMapper;
import com.zx.insectdetection.service.DetectService;
import com.zx.insectdetection.utils.ThreadLocalUtil;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/detect")
@Validated
public class DetectController {
    @Autowired
    private DetectService detectService;
    @Autowired
    private DetectMapper detectMapper;

    @PostMapping("/insertDetectInfo")
    public Result insertDetectInfo(@RequestBody List<DetectImgUrl> dataList) {
        for (DetectImgUrl data : dataList) {
            int imgId = detectService.addImgUrl(data.getImgUrl());
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            for (ResultObject result : data.getResult()) {
                detectService.insertDetectResult(result.getSort(),result.getConfidenceLevel(),imgId);
            }
        }
        return Result.success("检测记录已保存");
    }

    @PostMapping("/insertDetectImgUrl")
    public Result insertImgUrl(@RequestParam @URL String imgUrl){
        detectService.addImgUrl(imgUrl);
        return Result.success();
    }

    @GetMapping("/history")
    public Result<List<DetectImgInfo>> getdetecthistory(){
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        List<DetectImgInfo> detectHistory = detectService.getDetectHistory(id);
        return Result.success(detectHistory);
    }

    @GetMapping("/getUserDetectList")
    public Result<List<DetectImgInfo>> getUserDetectList() {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        List<DetectImgInfo> userDetectList = detectService.getUserDetectList(id);
        return Result.success(userDetectList);
    }

    @DeleteMapping("/deleteDetectRecord/{id}")
    public Result deleteDetectRecord(@PathVariable Integer id) {
        detectService.deleteDetectRecord(id);
        return Result.success();
    }
    //获取所有检测信息
    @GetMapping("/getAllDetectList")
    public Result<List<DetectImgInfo>> getAllDetectList() {
        List<DetectImgInfo> detectList = detectMapper.getAllDetectList();
        return Result.success(detectList);
    }

}
