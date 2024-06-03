package com.es2.vadebicicleta.externo.cartaocredito.model

import java.time.LocalDate

class CartaoDeCredito (
    val nomeTitular: String,
    val numero: String,
    val validade: LocalDate,
    val cvv: String
)
