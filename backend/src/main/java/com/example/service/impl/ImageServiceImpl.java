package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.entity.dto.Account;
import com.example.mapper.AccountMapper;
import com.example.service.ImageService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author Ll
 * @description: 图片处理的具体实现
 * @date 2024/7/17 下午6:21
 */
@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Resource
    private MinioClient minioClient;
    @Resource
    private AccountMapper mapper;

    /**
     * @description: 头像保存到minio，并将头像地址保存在mysql中
     * @param: [file, id]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/17 下午7:43
     */
    @Override
    public String saveAvatar(MultipartFile file, int id) throws IOException {
        String imageName ="/avatar/" + UUID.randomUUID().toString().replace("-","");
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("forum")
                .stream(file.getInputStream(),file.getSize(),-1)
                .object(imageName)
                .build();
        try {
            minioClient.putObject(args);
            if(mapper.update(null, Wrappers.<Account>update().eq("id",id).set("avatar", imageName))>0){
                return imageName;
            }else return null;
        }catch (Exception e){
            String message = "图片上传失败:" + e.getMessage();
            log.error(message , e);
            return null;
        }
    }

    /**
     * @description: 从minio获取保存的头像
     * @param: [outputStream, ImageName]
     * @return: void
     * @author Ll
     * @date: 2024/7/18 上午8:55
     */
    @Override
    public void fetchAvatarFromMinio(OutputStream outputStream, String ImageName) throws Exception {
        GetObjectArgs args = GetObjectArgs
                .builder()
                .bucket("forum")
                .object(ImageName)
                .build();
        GetObjectResponse response = minioClient.getObject(args);
        IOUtils.copy(response,outputStream);
    }
}
