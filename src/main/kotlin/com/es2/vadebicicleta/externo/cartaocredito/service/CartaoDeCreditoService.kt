package com.es2.vadebicicleta.externo.cartaocredito.service

import com.es2.vadebicicleta.externo.cartaocredito.client.OperadoraClient
import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito
import org.springframework.stereotype.Service

@Service
class CartaoDeCreditoService(val operadoraCartaoDeCreditoClient: OperadoraClient) {

    fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) {
        val resposta = operadoraCartaoDeCreditoClient.validarCartaoDeCredito(cartaoDeCredito)
        if(!resposta.valido) {
            throw InvalidCreditCardException(cartaoDeCredito, resposta.erros)
        }
    }
}
