package com.es2.vadebicicleta.externo.cartaocredito.client

import com.es2.vadebicicleta.externo.cartaocredito.client.dto.CartaoDeCreditoCobrancaDto
import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito
import com.es2.vadebicicleta.externo.commons.exception.ExternalServiceException
import io.github.oshai.kotlinlogging.KotlinLogging
import net.authorize.Environment
import net.authorize.api.contract.v1.*
import net.authorize.api.controller.CreateTransactionController
import net.authorize.api.controller.base.ApiOperationBase
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

import java.math.BigDecimal


interface OperadoraClient {
    fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoValidacao
    fun enviarCobranca(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoCobrancaResposta
}

private val logger = KotlinLogging.logger {}

@Service
class OperadoraClientDefaultImpl(
    @Value("\${vadebicicleta.usar-servicos-reais}")
    val servicosReais : Boolean,
    @Value("\${vadebicicleta.cartao-de-credito.operadora.id}")
    private val loginId : String,
    @Value("\${vadebicicleta.cartao-de-credito.operadora.key}")
    private val transactionKey : String
) : OperadoraClient {

    init {
        if(servicosReais) {
            val merchantAuthenticationType = MerchantAuthenticationType()
            merchantAuthenticationType.name = loginId
            merchantAuthenticationType.transactionKey = transactionKey
            ApiOperationBase.setMerchantAuthentication(merchantAuthenticationType)
            ApiOperationBase.setEnvironment(Environment.SANDBOX)
        }
    }

    override fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoValidacao {
        if(!servicosReais) {
            return CartaoDeCreditoValidacao(true)
        }

        val creditCardPayment = getCreditCardPaymentType(cartaoDeCredito)

        val transactionType = TransactionTypeEnum.AUTH_ONLY_TRANSACTION.value()
        val amount = BigDecimal.ZERO

        val response = doRequest(transactionType, creditCardPayment, amount)

        if(response.messages.resultCode != MessageTypeEnum.OK) {
            throw ExternalServiceException("Erro na integracao com Authorize.Net. ${response.messages.message}")
        }

        val result = response.transactionResponse

        if(!result.responseCode.equals("1") || !result.cvvResultCode.equals("M")) {
            return CartaoDeCreditoValidacao(false, listOf("Cartao de credito invalido"))
        }

        return CartaoDeCreditoValidacao(true)
    }

    override fun enviarCobranca(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoCobrancaResposta {
        if(!servicosReais) {
            return CartaoDeCreditoCobrancaResposta("SUCESSO")
        }

        val response : ResponseEntity<CartaoDeCreditoCobrancaDto> =
           ResponseEntity.ok().build()

        if(response.statusCode != HttpStatus.OK) {
            throw ExternalServiceException(
                "Conexão com a operadora de cartão de crédito mal sucedida. Código ${response.statusCode}")
        }

        return converteResponseCobranca(response.body ?: throw ExternalServiceException(
            "Erro inesperado na integracação com a operadora de cartão de crédtio (cobrança): resposta com body null"))
    }

    private fun doRequest(
        transactionType: String?,
        paymentType: PaymentType,
        amount: BigDecimal?,
    ): CreateTransactionResponse {

        val transactionObject = TransactionRequestType()
        transactionObject.transactionType = transactionType
        transactionObject.payment = paymentType
        transactionObject.amount = amount

        val request = CreateTransactionRequest()
        request.transactionRequest = transactionObject

        val controller = CreateTransactionController(request)
        controller.execute()

        if(controller.resultCode != MessageTypeEnum.OK) {
            throw ExternalServiceException("Erro na integração com Authorize.Net.")
        }

        val response = controller.apiResponse

        return response
    }

    fun getCreditCardPaymentType(cartaoDeCredito : CartaoDeCredito) : PaymentType {
        val numero = cartaoDeCredito.numero
        val dataDeVencimento = "${cartaoDeCredito.validade.month.value}${cartaoDeCredito.validade.year}"
        val cvv = cartaoDeCredito.cvv
        val creditCard = CreditCardType()

        creditCard.cardNumber = numero
        creditCard.expirationDate = dataDeVencimento
        creditCard.cardCode = cvv

        val paymentType = PaymentType()
        paymentType.creditCard = creditCard

        return paymentType
    }


    private fun converteResponseCobranca(body: CartaoDeCreditoCobrancaDto) : CartaoDeCreditoCobrancaResposta {
        return CartaoDeCreditoCobrancaResposta(
            body.status ?: throw ExternalServiceException(
                "Erro inesperado na integracação com a operadora de cartão de crédtio: campo \"status\" null"),
                body.erros ?: emptyList()
            )
    }
}