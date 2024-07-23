package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet

import com.es2.vadebicicleta.externo.dominio.CartaoDeCreditoCobrancaStatus
import com.es2.vadebicicleta.externo.dominio.CartaoDeCreditoValidacaoStatus
import com.es2.vadebicicleta.externo.cartaocredito.client.OperadoraCartaoDeCreditoClient
import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import com.es2.vadebicicleta.externo.commons.exception.ExternalServiceException
import com.es2.vadebicicleta.externo.dominio.Ciclista
import com.es2.vadebicicleta.externo.dominio.StatusPagamentoEnum
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
) : OperadoraCartaoDeCreditoClient {

    init {
        val merchantAuthenticationType = MerchantAuthenticationType()
        merchantAuthenticationType.name = loginId
        merchantAuthenticationType.transactionKey = transactionKey
        ApiOperationBase.setMerchantAuthentication(merchantAuthenticationType)
        ApiOperationBase.setEnvironment(Environment.SANDBOX)
    }

    override fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoValidacaoStatus {
        val creditCardPayment = getCreditCardPaymentType(cartaoDeCredito)
        val poNumber = "vl${Instant.now().nano}"

        val response = doRequest(
            TransactionTypeEnum.AUTH_ONLY_TRANSACTION, creditCardPayment,
            BigDecimal.valueOf(0.01), poNumber = poNumber)

        val erroList = errorsResponse(response)
        if(erroList.isNotEmpty()) {
            return CartaoDeCreditoValidacaoStatus(false, erroList)
        }

        val result = response.transactionResponse

        anularTransacao(result.transId)

        return CartaoDeCreditoValidacaoStatus(true)
    }

    override fun enviarCobranca(valor: BigDecimal, cartaoDeCredito: CartaoDeCredito, ciclista: Ciclista) :
            CartaoDeCreditoCobrancaStatus {
        val creditCardPayment = getCreditCardPaymentType(cartaoDeCredito)
        val documento = ciclista.cpf ?: ciclista.passaporte?.numero ?: "000000"
        val poNumber = documento.substring(0, 6) + Instant.now().nano

        val customer = getIndividualCustomer(ciclista)

        val processingOptions = ProcessingOptions()
        processingOptions.isIsSubsequentAuth = true
        processingOptions.isIsStoredCredentials = true

        val response = doRequest(
            TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION, creditCardPayment, valor,
            poNumber = poNumber, customer = customer
        )

        val erroList = errorsResponse(response)
        if(erroList.isNotEmpty()) {
            return CartaoDeCreditoCobrancaStatus(StatusPagamentoEnum.FALHA, erroList)
        }

        return CartaoDeCreditoCobrancaStatus(StatusPagamentoEnum.PAGA)
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
        poNumber: String? = null,
        customer: CustomerDataType? = null
    ): CreateTransactionResponse {

        val transactionObject = TransactionRequestType()
        transactionObject.transactionType = transactionType.value()
        transactionObject.payment = paymentType
        transactionObject.amount = amount
        transactionObject.refTransId = refTransId
        transactionObject.poNumber = poNumber
        transactionObject.customer = customer

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

private fun getIndividualCustomer(ciclista: Ciclista) : CustomerDataType {
    val customer = CustomerDataType()
    customer.type = CustomerTypeEnum.INDIVIDUAL
    customer.id = ciclista.id.toString()
    customer.email = ciclista.email

    return customer
}

private fun errorsResponse(response: CreateTransactionResponse): MutableList<String> {
    if (response.messages?.resultCode == null || response.messages.resultCode != MessageTypeEnum.OK) {
        throw ExternalServiceException("Erro na integracao com Authorize.Net. ${response.messages.message}")
    }

    val result =
        response.transactionResponse ?: throw ExternalServiceException("Erro na integracao com Authorize.Net.")

    val erroList = mutableListOf<String>()
    if (result.responseCode != "1") {
        erroList.add("Cartao de credito invalido")
    }
    if (result.cvvResultCode != "M") {
        erroList.add("CVV invalido. Authorize.Net")
    }
    return erroList
}

