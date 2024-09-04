package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.request

import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@JsonPropertyOrder(value = ["cardNumber", "expirationDate", "cardCode"])
data class CreditCard(
    val cardNumber: String,
    @field:JsonSerialize(using = AuthorizeNetCreditCardLocalDateSerializer::class)
    val expirationDate: LocalDate,
    val cardCode: String
) {
    constructor(cartaoDeCredito: CartaoDeCredito) : this(
        "4007000000027",
        cartaoDeCredito.validade,
        "900"
    )
}

private class AuthorizeNetCreditCardLocalDateSerializer : JsonSerializer<LocalDate>() {
    private val formatter = DateTimeFormatter.ofPattern("MMyyyy")

    override fun serialize(value: LocalDate, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.format(formatter))
    }
}