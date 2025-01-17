package com.nnk.springboot.controllers.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Interceptor configuration for request logging
 */
@Configuration
public class LogConfiguration implements WebMvcConfigurer {

    private RequestInterceptor requestInterceptor;

    @Autowired
    public LogConfiguration(RequestInterceptor requestInterceptor) {
        this.requestInterceptor = requestInterceptor;
    }

    /**
     * Adds requestInterceptor to all input requests
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor)
                .addPathPatterns("/**/")
                .excludePathPatterns("/css/**");
    }
}
