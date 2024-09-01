package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
@Configuration
data class AuthorizeNetConfig(
    @Value("\${vadebicicleta.cartao-de-credito.operadora.url}")
    val url: String,
    @Value("\${vadebicicleta.cartao-de-credito.operadora.id}")
    val id: String,
    @Value("\${vadebicicleta.cartao-de-credito.operadora.key}")
    val key: String)
