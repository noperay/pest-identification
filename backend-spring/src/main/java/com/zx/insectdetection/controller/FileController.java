package com.zx.insectdetection.controller;

import com.zx.insectdetection.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import com.zx.insectdetection.entity.others.Result;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public Result<String> upload(@RequestPart("file") MultipartFile file){
        String imgFileStr = fileService.upload(file);
        return Result.success(imgFileStr);
    }
}
