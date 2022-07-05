package com.wafflestudio.snuttev

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableBatchProcessing
class SnuttevBatchApplication

fun main(args: Array<String>) {
    runApplication<SnuttevBatchApplication>(*args)
}
