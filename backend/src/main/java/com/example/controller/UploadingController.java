package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.ImageService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Ll
 * @description: 上传头像、附件等接口
 * @date 2024/7/17 下午6:01
 */
@Slf4j
@RestController
@RequestMapping("/api/images")
public class UploadingController {

    @Resource
    ImageService service;

    @PostMapping("/avatar")
    public RestBean<String> uploadAvatar(@RequestParam("file") MultipartFile file,
                                         @RequestAttribute(Const.ATTR_USER_ID) int id) throws IOException {
        //判断头像大小，如果过大则报错
        if(file.getSize() > 1024 * 150){
            return RestBean.failure(400,"您的头像图片超过150KB");
        }
        log.info("正在进行头像上传......");
        String url = service.saveAvatar(file, id);
        if(url == null){
            return RestBean.failure(400,"头像上传失败,请联系管理员");
        }else{
            String s = "头像上传成功！头像大小:"+file.getSize();
            log.info(s);
            return RestBean.success(url);
        }
    }
}
