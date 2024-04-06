package com.lbh.openapi.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lib.openapi.filter.TraceLogFilter;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<TraceLogFilter> loggingFilter() {
        FilterRegistrationBean<TraceLogFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new TraceLogFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }

}
