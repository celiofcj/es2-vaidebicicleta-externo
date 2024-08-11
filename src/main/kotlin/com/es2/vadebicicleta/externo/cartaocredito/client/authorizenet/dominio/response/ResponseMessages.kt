package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.response

data class ResponseMessages(
    val resultCode: String?,
    val message: List<Message?>?
)