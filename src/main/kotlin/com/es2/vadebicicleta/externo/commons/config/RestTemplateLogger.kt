package com.es2.vadebicicleta.externo.commons.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

private val logger = KotlinLogging.logger {}

class RestTemplateLogger : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        logger.info { String(body) }
        val response: ClientHttpResponse = execution.execute(request, body)
        return response
    }
}