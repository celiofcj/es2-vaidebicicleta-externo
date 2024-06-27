package com.es2.vadebicicleta.externo.cobranca.model

import java.time.LocalDate

class CartaoDeCreditoRegistrado (
    val nomeTitular: String,
    val numero: String,
    val validade: LocalDate,
    val cvv: String
)