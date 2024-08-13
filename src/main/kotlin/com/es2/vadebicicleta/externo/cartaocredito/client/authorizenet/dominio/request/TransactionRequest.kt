package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = ["transactionType", "amount", "payment", "poNumber"])
data class TransactionRequest(
    val transactionType: TransactionType,
    val amount: BigDecimal? = null,
    val payment: Payment? = null,
    val poNumber: String? = null,
    val refTransId: String? = null,
)