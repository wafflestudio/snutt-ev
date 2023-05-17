package com.wafflestudio.snuttev.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.core.jackson.ModelResolver
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun modelResolver(objectMapper: ObjectMapper): ModelResolver {
        return ModelResolver(objectMapper)
    }

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI().info(
            Info()
                .title("Snutt Lecture Evaluation Service API definition")
                .description(
                    """
                    Responses 의 "user_id" 는 /ev-service/* 를 routing하는 환경에서 실제로는 아래와 같은 schema로 변환됩니다.
                    
                    "user": {
                        "id": "string",
                        "email": "string", (nullable)
                        "local_id": "string", (nullable)
                        "fb_name": "string" (nullable)
                    }
                """
                )
                .version("v0.0.1")
        )
    }
}
