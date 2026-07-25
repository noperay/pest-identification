package com.zx.insectdetection.service;

import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String upload(MultipartFile file);

}
