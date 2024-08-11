package com.es2.vadebicicleta.externo.commons.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class RestConfig {
    @Bean
    fun configRestTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.setConnectTimeout(Duration.ofSeconds(30))
            .setReadTimeout(Duration.ofSeconds(30))
            .build()
            .apply {
                interceptors = mutableListOf()
                interceptors.add(RestTemplateLogger())
            }
    }
}
