package com.es2.vadebicicleta.externo.cartaocredito.service

import com.es2.vadebicicleta.externo.cartaocredito.client.OperadoraClient
import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito
import org.springframework.stereotype.Service

@Service
class CartaoCreditoService(val operadoraClient: OperadoraClient) {

    fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) : Boolean {
        return operadoraClient.consultarCartaoDeCredito(cartaoDeCredito)
    }
}
