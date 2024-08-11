package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.request

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(value = ["name", "transactionKey"])
data class MerchantAuthentication(
    val name: String,
    val transactionKey: String
)