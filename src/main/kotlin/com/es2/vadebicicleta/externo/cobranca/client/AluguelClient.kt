package com.es2.vadebicicleta.externo.cobranca.client

import com.es2.vadebicicleta.externo.cartaocredito.controller.dto.CartaoDeCreditoConverter
import com.es2.vadebicicleta.externo.cartaocredito.controller.dto.CartaoDeCreditoInDto
import com.es2.vadebicicleta.externo.commons.exception.ExternalServiceException
import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import com.es2.vadebicicleta.externo.dominio.Ciclista
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AluguelClient(
    private val restTemplate: RestTemplate,
    @Value("\${vadebicicleta.aluguel.url}")
    private val url: String,
    private val cartaoDeCreditoConverter: CartaoDeCreditoConverter,
    )  {

    fun getCiclista(idCiclista: Long) : Ciclista? =
        getRequest("$url/ciclista/{idCiclista}", idCiclista)

    fun getCartaoDeCredito(idCiclista: Long): CartaoDeCredito? {
        val cartaoDeCreditoInDto : CartaoDeCreditoInDto = getRequest("$url/cartaoDeCredito/{idCiclista}", idCiclista)
            ?: return null

        return cartaoDeCreditoConverter.toObject(cartaoDeCreditoInDto)
    }

    private inline fun <reified T> getRequest(url: String, id: Number) =
        trataResponseGet(restTemplate.getForEntity(url, T::class.java, id))

    private fun <T> trataResponseGet(response : ResponseEntity<T>) =
        when(response.statusCode) {
            HttpStatus.OK -> response.body
            HttpStatus.NOT_FOUND -> null
            else -> throw ExternalServiceException("Houve um erro ao fazer a requisição ao serviço de aluguel." +
                " Status: ${response.statusCode}. Message: ${response.body}")
        }
}

