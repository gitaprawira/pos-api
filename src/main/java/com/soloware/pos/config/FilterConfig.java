package com.soloware.pos.config;

import com.soloware.pos.core.interceptor.MDCFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Filter configuration for registering custom filters
 */
@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final MDCFilter mdcFilter;

    @Bean
    public FilterRegistrationBean<MDCFilter> mdcFilterRegistration() {
        FilterRegistrationBean<MDCFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(mdcFilter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // Execute first
        registration.setName("mdcFilter");
        return registration;
    }
}
