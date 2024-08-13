package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.request

import com.es2.vadebicicleta.externo.commons.serializer.ToStringBasedEnumSerializer
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonPropertyOrder(value = ["type", "id", "email"])
data class Customer(
    val id: String,
    val type: CustomerType,
    val email: String
)

@JsonSerialize(using = ToStringBasedEnumSerializer::class)
enum class CustomerType {
    INDIVIDUAL,
    BUSSINESS;

    override fun toString() = when(this) {
        INDIVIDUAL -> "individual"
        BUSSINESS -> "business"
    }
}
