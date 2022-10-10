package com.wafflestudio.snuttev

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class SnuttevApplication

fun main(args: Array<String>) {
    runApplication<SnuttevApplication>(*args)
}
