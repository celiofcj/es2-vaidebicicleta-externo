package com.es2.vadebicicleta.externo.cartaocredito.client

import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito

interface OperadoraCartaoDeCreditoClient {
    fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoValidacao
    fun enviarCobranca(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoCobrancaResposta
}