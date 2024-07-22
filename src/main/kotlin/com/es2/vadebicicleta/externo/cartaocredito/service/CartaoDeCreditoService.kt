 package com.es2.vadebicicleta.externo.cartaocredito.service

import com.es2.vadebicicleta.externo.cartaocredito.client.CartaoDeCreditoCobrancaResposta
import com.es2.vadebicicleta.externo.cartaocredito.client.OperadoraCartaoDeCreditoClient
import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import com.es2.vadebicicleta.externo.dominio.StatusPagamentoEnum
import com.es2.vadebicicleta.externo.commons.exception.ExternalServiceException
import org.springframework.stereotype.Service

@Service
class CartaoDeCreditoService(val operadoraCartaoDeCreditoClient: OperadoraCartaoDeCreditoClient) {

    fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) {
        val resposta = operadoraCartaoDeCreditoClient.validarCartaoDeCredito(cartaoDeCredito)
        if(!resposta.valido) {
            throw InvalidCreditCardException(cartaoDeCredito, resposta.erros)
        }
    }

    fun enviarCobranca(cartaoDeCredito: CartaoDeCredito) : StatusPagamentoEnum {
        val cobrancaResposta : CartaoDeCreditoCobrancaResposta

        try {
            cobrancaResposta = operadoraCartaoDeCreditoClient.enviarCobranca(cartaoDeCredito)
        } catch (externalServiceException: ExternalServiceException) {
            return StatusPagamentoEnum.FALHA
        }

        return when (cobrancaResposta.status) {
            "SUCESSO" -> StatusPagamentoEnum.PAGA
            "FRACASSO" -> StatusPagamentoEnum.FALHA
            else -> throw IllegalArgumentException("Status do pagamento inválido")
        }
    }
}
