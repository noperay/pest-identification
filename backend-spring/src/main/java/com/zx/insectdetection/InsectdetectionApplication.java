package com.zx.insectdetection;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@MapperScan("com.zx.insectdetection.mapper")
@EnableJpaRepositories(basePackages = "com.zx.insectdetection.repository")
@EntityScan(basePackages = "com.zx.insectdetection.entity")
public class InsectdetectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(InsectdetectionApplication.class, args);
    }

}
