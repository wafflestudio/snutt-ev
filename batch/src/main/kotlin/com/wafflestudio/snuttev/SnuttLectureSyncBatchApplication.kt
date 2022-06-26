package com.wafflestudio.snuttev

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableBatchProcessing
class SnuttLectureSyncBatchApplication

fun main(args: Array<String>) {
    runApplication<SnuttLectureSyncBatchApplication>(*args)
}
