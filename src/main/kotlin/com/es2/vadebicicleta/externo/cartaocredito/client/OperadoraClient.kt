package com.es2.vadebicicleta.externo.cartaocredito.client

import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito
import com.es2.vadebicicleta.externo.cartaocredito.model.StatusPagamentoEnum
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestOperations

interface OperadoraClient {
    fun consultarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) : Boolean
    fun enviarCobranca(cartaoDeCredito: CartaoDeCredito) : StatusPagamentoEnum
}


@Component
class DefaultOperadoraClientImpl(val restOperations: RestOperations,
                                 @Value("\${cartao-de-credito.url-operadora}")
                                 val urlOperadora: String) : OperadoraClient {
    override fun consultarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) : Boolean{
//        val resposta : ResponseEntity<Unit> = restOperations.postForEntity(urlOperadora, cartaoDeCredito)

        return true
    }

    override fun enviarCobranca(cartaoDeCredito: CartaoDeCredito) : StatusPagamentoEnum{
        TODO("Not yet implemented")
    }
}