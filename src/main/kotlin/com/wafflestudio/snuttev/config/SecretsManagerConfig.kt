package com.wafflestudio.snuttev.config

import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.EnvironmentAware
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment

@Configuration
@Profile("dev | prod")
class SecretsManagerConfig : EnvironmentAware, BeanFactoryPostProcessor {
    private lateinit var env: Environment

    override fun setEnvironment(environment: Environment) {
        env = environment
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val secretNames = env.getProperty("secretNames", "").split(",")
        val region = "ap-northeast-2"
        val objectMapper = jacksonObjectMapper()

        secretNames.forEach { secretName ->
            val secretString = getSecretString(secretName, region)
            val map = objectMapper.readValue<Map<String, String>>(secretString)
            map.forEach { (key, value) -> System.setProperty(key, value) }
        }
    }

    fun getSecretString(secretName: String, region: String): String {
        val client = AWSSecretsManagerClientBuilder.standard().withRegion(region).build()
        val request = GetSecretValueRequest().withSecretId(secretName)
        return client.getSecretValue(request).secretString
    }
}
