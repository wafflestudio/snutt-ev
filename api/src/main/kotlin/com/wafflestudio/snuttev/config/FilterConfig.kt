package com.wafflestudio.snuttev.config

import jakarta.servlet.Filter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterConfig {

    @Bean
    fun filterRegistrationBean(): FilterRegistrationBean<Filter> {
        val filterRegistrationBean = FilterRegistrationBean<Filter>(SnuttUserFilter())
        filterRegistrationBean.addUrlPatterns("/v1/*")
        return filterRegistrationBean
    }
}
