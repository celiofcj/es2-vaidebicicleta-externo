package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet

import com.es2.vadebicicleta.externo.cartaocredito.client.CartaoDeCreditoCobrancaResposta
import com.es2.vadebicicleta.externo.cartaocredito.client.CartaoDeCreditoValidacao
import com.es2.vadebicicleta.externo.cartaocredito.client.OperadoraCartaoDeCreditoClient
import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito
import com.es2.vadebicicleta.externo.commons.exception.ExternalServiceException
import net.authorize.Environment
import net.authorize.api.contract.v1.*
import net.authorize.api.controller.CreateTransactionController
import net.authorize.api.controller.base.ApiOperationBase
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant

@Service
class AuthorizeNetCartaoDeCreditoClient(
    @Value("\${vadebicicleta.cartao-de-credito.operadora.id}")
    private val loginId: String,
    @Value("\${vadebicicleta.cartao-de-credito.operadora.key}")
    private val transactionKey: String,
    private val authorizeNetMetadataRepository: AuthorizeNetMetadataRepository
) : OperadoraCartaoDeCreditoClient {

    init {
        val merchantAuthenticationType = MerchantAuthenticationType()
        merchantAuthenticationType.name = loginId
        merchantAuthenticationType.transactionKey = transactionKey
        ApiOperationBase.setMerchantAuthentication(merchantAuthenticationType)
        ApiOperationBase.setEnvironment(Environment.SANDBOX)
    }

    override fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoValidacao {
        val creditCardPayment = getCreditCardPaymentType(cartaoDeCredito)
        val processingOptions = ProcessingOptions()
        processingOptions.isIsFirstSubsequentAuth = true
        val poNumber = "vl${Instant.now().nano}"

        val response = doRequest(
            TransactionTypeEnum.AUTH_ONLY_TRANSACTION, creditCardPayment,
            BigDecimal.valueOf(0.01), processingOptions = processingOptions, poNumber = poNumber)

        if(response.messages?.resultCode == null || response.messages.resultCode != MessageTypeEnum.OK) {
            throw ExternalServiceException("Erro na integracao com Authorize.Net. ${response.messages.message}")
        }

        val result = response.transactionResponse ?:
            throw ExternalServiceException("Erro na integracao com Authorize.Net.")

        val erroList = mutableListOf<String>()
        if(!result.responseCode.equals("1")) {
            erroList.add("Cartao de credito invalido")
        }
        if(!result.cvvResultCode.equals("M")) {
            erroList.add("CVV invalido. Authorize.Net")
        }
        if(erroList.isNotEmpty()) {
            return CartaoDeCreditoValidacao(false, erroList)
        }

        anularTransacao(result.transId)

        persisteMetadata(cartaoDeCredito.numero, result.networkTransId)

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
        processingOptions: ProcessingOptions? = null,
        arrayOfLineItem: ArrayOfLineItem? = null,
        poNumber: String? = null
    ): CreateTransactionResponse {

        val transactionObject = TransactionRequestType()
        transactionObject.transactionType = transactionType.value()
        transactionObject.payment = paymentType
        transactionObject.amount = amount
        transactionObject.refTransId = refTransId
        transactionObject.poNumber = purchaseOrderNumber
        transactionObject.processingOptions = processingOptions
        transactionObject.lineItems = arrayOfLineItem
        transactionObject.poNumber = poNumber
        val request = CreateTransactionRequest()
        request.transactionRequest = transactionObject

        val controller = CreateTransactionController(request)
        controller.execute()

        return controller.apiResponse ?: throw ExternalServiceException("Erro na integração com Authorize.Net.")
    }

    private fun persisteMetadata(
        numeroCartaoDeCredito: String,
        networkTransId: String,
    ) {
        val salvo = authorizeNetMetadataRepository.findByCardNumber(numeroCartaoDeCredito)

        if(salvo != null) {
            salvo.networkTransId = networkTransId
            authorizeNetMetadataRepository.save(salvo)
            return
        }

        val novo = AuthorizeNetMetadata(numeroCartaoDeCredito, networkTransId)
        authorizeNetMetadataRepository.save(novo)
    }
}

