package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.request

import com.es2.vadebicicleta.externo.commons.serializer.ToStringBasedEnumSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize(using = ToStringBasedEnumSerializer::class)
enum class TransactionType {
    AUTH_ONLY_TRANSACTION,
    AUTH_CREDIT_CARD,
    CAPTURE_PREV_AUTH_CREDIT_CARD,
    VOID_TRANSACTION;

    override fun toString() = when(this) {
        AUTH_ONLY_TRANSACTION -> "authCaptureTransaction"
        AUTH_CREDIT_CARD -> "authOnlyTransaction"
        CAPTURE_PREV_AUTH_CREDIT_CARD -> "priorAuthCaptureTransaction"
        VOID_TRANSACTION -> "voidTransaction"
    }
}