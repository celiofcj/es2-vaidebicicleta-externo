package com.es2.vadebicicleta.externo.cartaocredito.client

import com.es2.vadebicicleta.externo.dominio.StatusPagamentoEnum

data class CartaoDeCreditoCobrancaResposta(
    val status: StatusPagamentoEnum,
    val erros: List<String>? = emptyList()
)
