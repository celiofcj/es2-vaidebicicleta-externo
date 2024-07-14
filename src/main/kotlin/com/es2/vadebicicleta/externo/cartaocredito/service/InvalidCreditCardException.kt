package com.es2.vadebicicleta.externo.cartaocredito.service

import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito

class InvalidCreditCardException(val cartaoDeCredito: CartaoDeCredito?, val errors: List<String>?) : RuntimeException()