package com.es2.vadebicicleta.externo.cartaocredito.client

import com.es2.vadebicicleta.externo.cartaocredito.client.dto.CartaoDeCreditoValidacaoDto
import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito
import com.es2.vadebicicleta.externo.cartaocredito.model.StatusPagamentoEnum
import com.es2.vadebicicleta.externo.commons.exception.ExternalServiceException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations

interface OperadoraClient {
    fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoValidacao
    fun enviarCobranca(cartaoDeCredito: CartaoDeCredito) : StatusPagamentoEnum
}

@Service
class OperadoraClientDefaultImpl(
    val restOperations: RestOperations,
     @Value("\${vadebicicleta.cartao-de-credito.operadora.url}")
     val urlOperadora: String
    ) : OperadoraClient {

    override fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoValidacao {
        val response : ResponseEntity<CartaoDeCreditoValidacaoDto> = restOperations
            .postForEntity(urlOperadora, cartaoDeCredito, CartaoDeCreditoValidacaoDto::class.java)

        if(response.statusCode != HttpStatus.OK) {
            throw ExternalServiceException(
                "Conexão com a operadora de cartão de crédito mal sucedida. Código ${response.statusCode}")
        }

        return converteResponse(response.body ?: throw ExternalServiceException(
            "Erro inesperado na integracação com a operadora de cartão de crédtio: resposta com body null"))
    }

    override fun enviarCobranca(cartaoDeCredito: CartaoDeCredito) : StatusPagamentoEnum{
        TODO("Not yet implemented")
    }

    private fun converteResponse(body: CartaoDeCreditoValidacaoDto): CartaoDeCreditoValidacao {
        return CartaoDeCreditoValidacao(
            body.valido ?: throw ExternalServiceException(
            "Erro inesperado na integracação com a operadora de cartão de crédtio: campo \"valido\" null"),
            body.erros ?: emptyList()
            )
    }
}