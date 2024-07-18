package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

/**
 * @author Ll
 * @description: 加密
 * @date 2024/7/12 上午10:43
 */
@Configuration
public class WebConfiguration {

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
