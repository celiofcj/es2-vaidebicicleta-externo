package com.es2.vadebicicleta.externo.dominio

import java.time.LocalDate

data class CartaoDeCredito (
    val nomeTitular: String,
    val numero: String,
    val validade: LocalDate,
    val cvv: String
)
