package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.response

import com.fasterxml.jackson.annotation.JsonProperty

data class TransactionResponse(
    val responseCode: String?,
    val authCode: String?,
    val avsResultCode: String?,
    val cvvResultCode: String?,
    val cavvResultCode: String?,
    val transId: String?,
    val refTransID: String?,
    val transHash: String?,
    val testRequest: String?,
    val accountNumber: String?,
    val accountType: String?,
    val messages: Messages?,
    val errors: List<ErrorDetail?>?,
    val transHashSha2: String?,
    @JsonProperty("SupplementalDataQualificationIndicator")
    val supplementalDataQualificationIndicator: Int?,
    val networkTransId: String?

)