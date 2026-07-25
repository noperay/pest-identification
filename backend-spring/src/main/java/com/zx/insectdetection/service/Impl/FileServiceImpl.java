package com.zx.insectdetection.service.Impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.zx.insectdetection.config.OSSConfig;
import com.zx.insectdetection.service.FileService;
import com.zx.insectdetection.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 文件上传业务类
 *
 * @author lixiang
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {


    @Autowired
    private OSSConfig ossConfig;
    /**
     * 阿里云OSS文件上传
     *
     * @param file
     */
    @Override
    public String upload(MultipartFile file) {

        //获取相关配置
        String bucketName = ossConfig.getBucketName();
        String endPoint = ossConfig.getEndPoint();
        String accessKeyId = ossConfig.getAccessKeyId();
        String accessKeySecret = ossConfig.getAccessKeySecret();

        //创建OSS对象
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        //获取原生文件名
        String originalFilename = file.getOriginalFilename();

        //拼装OSS上存储的路径
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 生成唯一的文件名
        String fileName = generateUUID() + extension;

        // 获取当前日期并格式化为 '年/月/日'
        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());

        // 在 OSS 上 bucket 下的文件名
        String uploadFileName = datePath + "/" + fileName;

        try {
            PutObjectResult result = ossClient.putObject(bucketName, uploadFileName, file.getInputStream());
            //拼装返回路径
            if (result != null) {
                return "https://"+bucketName+"."+endPoint+"/"+uploadFileName;
            }
        } catch (IOException e) {
            log.error("文件上传失败:{}",e.getMessage());
        } finally {
            //OSS关闭服务
            ossClient.shutdown();
        }
        return null;
    }


    /**
     * 获取随机字符串
     * @return
     */
    private String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }
}