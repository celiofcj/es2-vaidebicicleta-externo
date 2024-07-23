 package com.es2.vadebicicleta.externo.cartaocredito.service

import com.es2.vadebicicleta.externo.dominio.CartaoDeCreditoCobrancaStatus
import com.es2.vadebicicleta.externo.cartaocredito.client.OperadoraCartaoDeCreditoClient
import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import com.es2.vadebicicleta.externo.dominio.Ciclista
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class CartaoDeCreditoService(val operadoraCartaoDeCreditoClient: OperadoraCartaoDeCreditoClient) {

    fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) {
        val resposta = operadoraCartaoDeCreditoClient.validarCartaoDeCredito(cartaoDeCredito)
        if(!resposta.valido) {
            throw InvalidCreditCardException(cartaoDeCredito, resposta.erros)
        }
    }

    fun enviarCobranca(valor: BigDecimal, cartaoDeCredito: CartaoDeCredito, ciclista: Ciclista)
    : CartaoDeCreditoCobrancaStatus {

        return operadoraCartaoDeCreditoClient.enviarCobranca(valor, cartaoDeCredito, ciclista)
    }
}
