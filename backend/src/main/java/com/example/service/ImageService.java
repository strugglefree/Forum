package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.StoreImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Ll
 * @description: 图片处理
 * @date 2024/7/17 下午6:19
 */
public interface ImageService extends IService<StoreImage> {
    String saveAvatar(MultipartFile file , int id ) throws IOException;
    String saveImage(MultipartFile file , int id ) throws IOException;
    void fetchAvatarFromMinio(OutputStream outputStream , String ImageName ) throws Exception;
}
