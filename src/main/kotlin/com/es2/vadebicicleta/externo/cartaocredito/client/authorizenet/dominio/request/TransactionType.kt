package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.request

import com.es2.vadebicicleta.externo.commons.serializer.ToStringBasedEnumSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize(using = ToStringBasedEnumSerializer::class)
enum class TransactionType {
    AUTH_CAPTURE_TRANSACTION,
    AUTH_ONLY_TRANSACTION,
    PRIOR_AUTH_CAPTURE_TRANSACTION,
    VOID_TRANSACTION;

    override fun toString() = when(this) {
        AUTH_CAPTURE_TRANSACTION -> "authCaptureTransaction"
        AUTH_ONLY_TRANSACTION -> "authOnlyTransaction"
        PRIOR_AUTH_CAPTURE_TRANSACTION -> "priorAuthCaptureTransaction"
        VOID_TRANSACTION -> "voidTransaction"
    }
}