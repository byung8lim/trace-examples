package com.lbh.openapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.lbh.openapi.interceptor.TraceLogInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Autowired
    private TraceLogInterceptor traceLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(traceLogInterceptor);
    }
}
