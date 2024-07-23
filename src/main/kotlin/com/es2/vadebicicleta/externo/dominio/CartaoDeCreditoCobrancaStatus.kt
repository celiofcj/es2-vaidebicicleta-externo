package com.es2.vadebicicleta.externo.dominio

data class CartaoDeCreditoCobrancaStatus(
    val status: StatusPagamentoEnum,
    val erros: List<String>? = emptyList()
)
