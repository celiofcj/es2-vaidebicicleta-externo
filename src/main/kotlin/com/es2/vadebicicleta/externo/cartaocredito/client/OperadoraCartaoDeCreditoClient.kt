package com.es2.vadebicicleta.externo.cartaocredito.client

import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import com.es2.vadebicicleta.externo.dominio.Ciclista
import java.math.BigDecimal

interface OperadoraCartaoDeCreditoClient {
    fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoValidacao
    fun enviarCobranca(valor: BigDecimal, cartaoDeCredito: CartaoDeCredito, ciclista: Ciclista)
    : CartaoDeCreditoCobrancaResposta
}