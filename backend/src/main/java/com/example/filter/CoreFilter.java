package com.example.filter;

import com.example.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Ll
 * @description: 跨域请求过滤器
 * @date 2024/7/12 下午2:35
 */
@Component
@Order(Const.ORDER_CORE)
public class CoreFilter extends HttpFilter {

    @Value("${spring.web.cors.origin}")
    String origin;

    @Value("${spring.web.cors.credentials}")
    boolean credentials;

    @Value("${spring.web.cors.methods}")
    String methods;

    @Override
    public void doFilter(HttpServletRequest request,
                         HttpServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        this.addCoreHeader(response,request);
        chain.doFilter(request,response);
    }

    public void addCoreHeader(HttpServletResponse response , HttpServletRequest request) {
        response.setHeader("Access-Control-Allow-Origin", this.resolveOrigin(request));
        response.setHeader("Access-Control-Allow-Methods",this.resolveMethod());
        response.setHeader("Access-Control-Allow-Headers","Authorization, Content-Type");
        if(credentials) {
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }
    }
    /**
     * @description: 解析配置文件中的请求方法
     * @param: []
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/14 下午2:11
     */
    private String resolveMethod(){
        return methods.equals("*") ? "GET, HEAD, POST, PUT, DELETE, OPTIONS, TRACE, PATCH" : methods;
    }

    /**
     * @description: 解析配置文件中的请求原始站点
     * @param: [request]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/14 下午2:12
     */
    private String resolveOrigin(HttpServletRequest request){
        return origin.equals("*") ? request.getHeader("Origin") : origin;
    }
}
