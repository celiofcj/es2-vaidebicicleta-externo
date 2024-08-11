package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.response

data class CreateTransactionResponse (
    val transactionResponse: TransactionResponse?,
    val refId: String?,
    val messages: ResponseMessages?
)