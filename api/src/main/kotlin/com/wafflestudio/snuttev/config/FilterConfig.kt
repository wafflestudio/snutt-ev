package com.wafflestudio.snuttev.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.servlet.Filter

@Configuration
class FilterConfig {

    @Bean
    fun filterRegistrationBean(): FilterRegistrationBean<Filter> {
        val filterRegistrationBean = FilterRegistrationBean<Filter>(SnuttUserFilter())
        filterRegistrationBean.addUrlPatterns("/v1/*")
        return filterRegistrationBean
    }
}
