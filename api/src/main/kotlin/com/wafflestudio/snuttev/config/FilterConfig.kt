package com.wafflestudio.snuttev.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterConfig {

    @Bean
    fun filterRegistrationBean(): FilterRegistrationBean<SnuttUserFilter> {
        val filterRegistrationBean = FilterRegistrationBean(SnuttUserFilter())
        filterRegistrationBean.addUrlPatterns("/v1/*")
        return filterRegistrationBean
    }
}
