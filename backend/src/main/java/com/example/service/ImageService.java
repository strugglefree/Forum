package com.example.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Ll
 * @description: 图片处理
 * @date 2024/7/17 下午6:19
 */
public interface ImageService {
    String saveAvatar(MultipartFile file , int id ) throws IOException;
    void fetchAvatarFromMinio(OutputStream outputStream , String ImageName ) throws Exception;
}
