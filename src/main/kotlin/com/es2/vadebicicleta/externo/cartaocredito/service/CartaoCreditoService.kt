package com.es2.vadebicicleta.externo.cartaocredito.service

import com.es2.vadebicicleta.externo.cartaocredito.client.OperadoraClient
import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CartaoCreditoService(val operadoraCartaoDeCreditoClient: OperadoraClient) {

    fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) {
        val resposta = operadoraCartaoDeCreditoClient.consultarCartaoDeCredito(cartaoDeCredito)
        if(!resposta.valido) {
            throw InvalidCreditCardException(cartaoDeCredito, resposta.erros)
        }
    }
}
