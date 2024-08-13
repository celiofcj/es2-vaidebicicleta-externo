package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.response

data class CreateTransactionResponse (
    val transactionResponse: TransactionResponse? = null,
    val refId: String? = null,
    val messages: Messages? = null
)