package com.xchat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /images/** 请求映射到本地 uploads 目录
        String uploadDir = "file:" + System.getProperty("user.dir") + "/uploads/";
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadDir);
    }
}