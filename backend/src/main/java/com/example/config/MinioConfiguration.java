package com.example.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ll
 * @description: minio的配置类
 * @date 2024/7/17 下午7:11
 */
@Slf4j
@Configuration
public class MinioConfiguration {
    @Value("${spring.web.minio.endpoint}")
    private String endpoint;
    @Value("${spring.web.minio.username}")
    private String username;
    @Value("${spring.web.minio.password}")
    private String password;

    @Bean
    public MinioClient minioClient(){
        log.info("Init Minio Client...");
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(username,password)
                .build();
    }
}
