package com.es2.vadebicicleta.externo.cartaocredito.client

import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import com.es2.vadebicicleta.externo.dominio.CartaoDeCreditoCobrancaStatus
import com.es2.vadebicicleta.externo.dominio.CartaoDeCreditoValidacaoStatus
import com.es2.vadebicicleta.externo.dominio.Ciclista
import java.math.BigDecimal

interface OperadoraCartaoDeCreditoClient {
    fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoValidacaoStatus
    fun enviarCobranca(valor: BigDecimal, cartaoDeCredito: CartaoDeCredito, ciclista: Ciclista)
    : CartaoDeCreditoCobrancaStatus
}