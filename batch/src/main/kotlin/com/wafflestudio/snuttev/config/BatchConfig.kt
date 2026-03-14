package com.wafflestudio.snuttev.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import javax.sql.DataSource

@Configuration
class BatchConfig {
    @Bean("batchDataSource")
    fun batchDataSource(): DataSource =
        EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("/org/springframework/batch/core/schema-h2.sql")
            .generateUniqueName(true)
            .build()

    /**
     * Mimic [org.springframework.boot.jdbc.autoconfigure.DataSourceConfiguration.Hikari]
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    fun dataSource(properties: DataSourceProperties): HikariDataSource? =
        properties
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build()
}
