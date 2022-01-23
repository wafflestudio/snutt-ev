package com.wafflestudio.snuttev.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.core.jackson.ModelResolver
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun modelResolver(objectMapper: ObjectMapper?): ModelResolver {
        return ModelResolver(objectMapper)
    }

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI().info(Info()
                .title("Snutt Evaluation Service API definition")
                .description("""
                    Responses 의 user_id는 Snutt 서버에 의해, 아래와 같은 DTO로 변환됩니다.
                    
                    "user": {
                        "is_admin": false,
                        "reg_date": "2022-01-23T00:00:00.000Z",
                        "notification_checked_at": "2021-07-24T08:28:34.804Z",
                        "email": "bdv111@wafflestudio.com",
                        "local_id": "bdv111",
                        "fb_name": null
                    }
                """)
                .version("v0.0.1")
        )
    }
}
