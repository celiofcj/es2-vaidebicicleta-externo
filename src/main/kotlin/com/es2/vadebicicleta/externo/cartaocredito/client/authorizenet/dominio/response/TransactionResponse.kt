package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.response

import com.fasterxml.jackson.annotation.JsonProperty

data class TransactionResponse(
    val responseCode: String? = null,
    val authCode: String? = null,
    val avsResultCode: String? = null,
    val cvvResultCode: String? = null,
    val cavvResultCode: String? = null,
    val transId: String? = null,
    val refTransID: String? = null,
    val transHash: String? = null,
    val testRequest: String? = null,
    val accountNumber: String? = null,
    val accountType: String? = null,
    val messages: List<Message?>? = null,
    val errors: List<ErrorDetail?>? = null,
    val transHashSha2: String? = null,
    @JsonProperty("SupplementalDataQualificationIndicator")
    val supplementalDataQualificationIndicator: Int? = null,
    val networkTransId: String? = null

)