package com.wafflestudio.snuttev.domain.common.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {

    @Operation(hidden = true)
    @GetMapping("/health_check")
    fun healthCheck() {

    }
}
