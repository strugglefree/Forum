package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.StoreImage;
import com.example.mapper.AccountMapper;
import com.example.mapper.ImageStoreMapper;
import com.example.service.ImageService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import io.minio.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author Ll
 * @description: 图片处理的具体实现
 * @date 2024/7/17 下午6:21
 */
@Slf4j
@Service
public class ImageServiceImpl extends ServiceImpl<ImageStoreMapper, StoreImage> implements ImageService {

    @Resource
    private MinioClient minioClient;
    @Resource
    private AccountMapper mapper;
    @Resource
    FlowUtils utils;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");


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
            String avatar = this.mapper.selectById(id).getAvatar();
            this.deleteOldAvatar(avatar);
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
     * @description: 保存图片到minio
     * @param: [file, id]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/19 上午11:10
     */
    @Override
    public String saveImage(MultipartFile file, int id) throws IOException {
        String key = Const.FORUM_IMAGE_COUNTER+id;
        if(!utils.limitPeriodCountCheck(key,20,3600))
            return null;
        String initName = UUID.randomUUID().toString().replace("-","");
        Date date = new Date();
        String imageName ="/cache/"+dateFormat.format(date)+initName;
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("forum")
                .stream(file.getInputStream(),file.getSize(),-1)
                .object(imageName)
                .build();
        try {
            minioClient.putObject(args);
            if(this.save(new StoreImage(id,imageName,date))){
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

    private void deleteOldAvatar(String avatar) throws Exception {
        if(avatar==null || avatar.isEmpty()) return;
        RemoveObjectArgs remove = RemoveObjectArgs.builder()
                .bucket("forum")
                .object(avatar)
                .build();
        minioClient.removeObject(remove);
    }
}
