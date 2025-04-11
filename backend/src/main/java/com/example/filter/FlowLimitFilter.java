package com.example.filter;

import com.example.entity.RestBean;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Ll
 * @description: 控制访问人数
 * @date 2024/7/14 上午11:15
 */
@Component
@Slf4j
@Order(Const.ORDER_LIMIT)
public class FlowLimitFilter extends HttpFilter {

    @Resource
    StringRedisTemplate template  ;
    @Resource
    FlowUtils utils;

    //指定时间内最大请求次数限制
    final int limit = 100;
    //计数时间周期
    final int period = 2;
    //超出请求限制封禁时间
    final int block = 30;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String address = request.getRemoteAddr();
        if (!"OPTIONS".equals(request.getMethod()) && !tryCount(address))
            this.writeBlockMessage(response);
        else
            chain.doFilter(request, response);
    }

    /**
     * @description: 限制访问
     * @param: [response]
     * @return: void
     * @author Ll
     * @date: 2024/7/14 上午11:40
     */
    private void writeBlockMessage(HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(RestBean.failure(429, "请求频率过快，请稍后再试").asJsonString());
    }
    /**
     * @description: 计数器，尝试计算在一定时间内的访问数量
     * @param: [address]
     * @return: boolean
     * @author Ll
     * @date: 2024/7/14 下午12:31
     */
    private boolean tryCount(String address) {
        synchronized (address.intern()) {
            if(Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_BLOCK + address)))
                return false;
            String counterKey = Const.FLOW_LIMIT_COUNTER + address;
            String blockKey = Const.FLOW_LIMIT_BLOCK + address;
            return utils.limitPeriodCheck(counterKey, blockKey, block, limit, period);
        }
    }
}
