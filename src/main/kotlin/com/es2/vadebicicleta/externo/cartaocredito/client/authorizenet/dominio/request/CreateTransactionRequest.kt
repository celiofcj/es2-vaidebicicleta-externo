package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.request

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(value = ["merchantAuthentication", "transactionRequest"])
data class CreateTransactionRequest(
    val merchantAuthentication: MerchantAuthentication,
    val transactionRequest: TransactionRequest
)

data class CreateTransactionRequestWrapped(
    val createTransactionRequest: CreateTransactionRequest
)