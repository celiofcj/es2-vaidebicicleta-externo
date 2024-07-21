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
        val processingOptions = ProcessingOptions()
        processingOptions.isIsFirstSubsequentAuth = true

        val response = doRequest(TransactionTypeEnum.AUTH_ONLY_TRANSACTION, creditCardPayment,
            BigDecimal.valueOf(0.01), purchaseOrderNumber = "1", processingOptions = processingOptions)

        if(response.messages?.resultCode == null || response.messages.resultCode != MessageTypeEnum.OK) {
            throw ExternalServiceException("Erro na integracao com Authorize.Net. ${response.messages.message}")
        }

        val result = response.transactionResponse ?: throw ExternalServiceException("Erro na integracao com Authorize.Net.")

        if(!result.responseCode.equals("1")) {
            return CartaoDeCreditoValidacao(false, listOf("Cartao de credito invalido"))
        }

        anularTransacao(result.transId)

        return CartaoDeCreditoValidacao(true)
    }

    override fun enviarCobranca(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoCobrancaResposta {
        return CartaoDeCreditoCobrancaResposta("SUCESSO")
    }

    private fun anularTransacao(refTransId: String) {
        val response = doRequest(TransactionTypeEnum.VOID_TRANSACTION, refTransId = refTransId)

        if(response.messages?.resultCode == null || response.messages.resultCode != MessageTypeEnum.OK) {
            throw ExternalServiceException("Erro na integracao com Authorize.Net. ${response.messages.message}")
        }
    }

    private fun doRequest(
        transactionType: TransactionTypeEnum,
        paymentType: PaymentType? = null,
        amount: BigDecimal? = null,
        refTransId: String? = null,
        purchaseOrderNumber: String? = null,
        processingOptions: ProcessingOptions? = null
    ): CreateTransactionResponse {

        val transactionObject = TransactionRequestType()
        transactionObject.transactionType = transactionType.value()
        transactionObject.payment = paymentType
        transactionObject.amount = amount
        transactionObject.refTransId = refTransId
        transactionObject.poNumber = purchaseOrderNumber
        transactionObject.processingOptions = processingOptions

        val request = CreateTransactionRequest()
        request.transactionRequest = transactionObject

        val controller = CreateTransactionController(request)
        controller.execute()

        return controller.apiResponse ?: throw ExternalServiceException("Erro na integração com Authorize.Net.")
    }
}

private fun getCreditCardPaymentType(cartaoDeCredito : CartaoDeCredito) : PaymentType {
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