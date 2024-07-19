package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.ImageService;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ll
 * @description: 获取头像的接口
 * @date 2024/7/17 下午7:57
 */
@Slf4j
@RestController
public class GetInformationController {

    @Resource
    ImageService service;

    @GetMapping("/images/**")
    public void AvatarFetch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.setHeader("Content-Type","image/jpeg");
        this.getAvatar(req, resp);
    }

    private void getAvatar(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String url = req.getServletPath().substring(7);
        ServletOutputStream outputStream = resp.getOutputStream();
        if(url.length() <= 13){
            resp.setStatus(404);
            outputStream.println(RestBean.failure(404,"Not found").toString());
        }else{
            try {
                service.fetchAvatarFromMinio(outputStream,url);
                resp.setHeader("Cache-Control", "max-age=2592000");
            }catch(ErrorResponseException e){
                if(e.response().code() == 404){
                    resp.setStatus(404);
                    outputStream.println(RestBean.failure(404,"Not found").toString());
                }else{
                    String s = "从minio中读取图片出现异常:"+e.getMessage();
                    log.error(s,e);
                }
            }
        }
    }
}
