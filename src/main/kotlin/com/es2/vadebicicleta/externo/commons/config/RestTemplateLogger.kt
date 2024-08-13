package com.es2.vadebicicleta.externo.commons.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


private val logger = KotlinLogging.logger {}

class RestTemplateLogger : ClientHttpRequestInterceptor {
    @Throws(IOException::class)
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        traceRequest(request, body)
        val response = execution.execute(request, body)
        traceResponse(response)
        return response
    }

    @Throws(IOException::class)
    private fun traceRequest(request: HttpRequest, body: ByteArray) {
        logger.debug{"===========================request begin================================================"}
        logger.debug{"URI         : $request.uri"}
        logger.debug{"Method      : $request.method"}
        logger.debug{"Headers     : $request.headers"}
        logger.debug{"Request body: ${String(body, charset("UTF-8"))}"}
        logger.debug{"==========================request end================================================"}
    }

    @Throws(IOException::class)
    private fun traceResponse(response: ClientHttpResponse) {
        val inputStringBuilder = StringBuilder()
        val bufferedReader = BufferedReader(InputStreamReader(response.body, "UTF-8"))
        var line = bufferedReader.readLine()
        while (line != null) {
            inputStringBuilder.append(line)
            inputStringBuilder.append('\n')
            line = bufferedReader.readLine()
        }
        logger.debug{"============================response begin=========================================="}
        logger.debug{"Status code  : $response.statusCode"}
        logger.debug{"Status text  : $response.statusText"}
        logger.debug{"Headers      : $response.headers"}
        logger.debug{"Response body: $inputStringBuilder"}
        logger.debug{"=======================response end================================================="}
    }
}