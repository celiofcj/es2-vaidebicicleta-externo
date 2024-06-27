package com.es2.vadebicicleta.externo.commons.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private val logger = KotlinLogging.logger {}

@ConfigurationProperties("email-receive")
@Component
data class EmailProperties(
    var protocol: String? = null,
    var username: String? = null,
    var password: String? = null,
    var host: String? = null,
    var port: String? = null,
    var mailbox: String? = null,
    var pollRate: Long = 30000,
) {
    fun url() : String {
        val usernameEncoded = URLEncoder.encode(username, StandardCharsets.UTF_8.name())
        val passwordEncoded = URLEncoder.encode(password, StandardCharsets.UTF_8.name())
        val s = "$protocol://$usernameEncoded:$passwordEncoded@$host:$port/$mailbox"
        logger.info { s }

        return s
    }
}