package com.wafflestudio.snuttev.config

import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class FlywayConfig(private val env: Environment) {
    @Bean(initMethod = "migrate")
    fun flyway(): Flyway {
        val url = env.getRequiredProperty("spring.datasource.url")
        val user = env.getRequiredProperty("spring.datasource.username")
        val password = env.getRequiredProperty("spring.datasource.password")
        val config = Flyway
            .configure()
            .dataSource(url, user, password)
        return Flyway(config)
    }
}
