package com.example.filter;

import com.example.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    @Override
    public void doFilter(HttpServletRequest request,
                         HttpServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        this.addCoreHeader(response,request);
        chain.doFilter(request,response);
    }

    public void addCoreHeader(HttpServletResponse response , HttpServletRequest request) {
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers","Authorization, Content-Type");
    }
}
