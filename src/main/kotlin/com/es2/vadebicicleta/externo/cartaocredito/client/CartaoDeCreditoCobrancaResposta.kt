package com.es2.vadebicicleta.externo.cartaocredito.client

data class CartaoDeCreditoCobrancaResposta(
    val status: String,
    val erros: List<String>? = emptyList()
)
