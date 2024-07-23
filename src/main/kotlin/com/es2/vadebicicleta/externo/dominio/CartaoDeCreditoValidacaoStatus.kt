package com.es2.vadebicicleta.externo.dominio

class CartaoDeCreditoValidacaoStatus(
    val valido : Boolean,
    val erros: List<String> = emptyList()
)