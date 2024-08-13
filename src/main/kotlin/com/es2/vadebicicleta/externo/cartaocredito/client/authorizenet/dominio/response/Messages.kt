package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.response

data class Messages(
    val resultCode: String? = null,
    val message: List<MessagesDetail?>? = null
)