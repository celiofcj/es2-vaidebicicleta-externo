package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet

import com.es2.vadebicicleta.externo.cartaocredito.client.OperadoraCartaoDeCreditoClient
import com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.request.*
import com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.response.CreateTransactionResponse
import com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.response.TransactionResponse
import com.es2.vadebicicleta.externo.commons.exception.ExternalServiceException
import com.es2.vadebicicleta.externo.dominio.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.time.Instant

@Service
class AuthorizeNetJsonClient(
    private val authorizeNetConfig: AuthorizeNetConfig,
    private val restTemplate: RestTemplate) : OperadoraCartaoDeCreditoClient {
    private final val mensagemErroPadrao = "Erro na integracao com Authorize.Net."
        
        
    override fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito): CartaoDeCreditoValidacaoStatus {
        val creditCard = CreditCard(cartaoDeCredito)
        val poNumber = "vl${Instant.now().nano}"

        val transactionObject = TransactionRequest(
            TransactionType.AUTH_ONLY_TRANSACTION,
            BigDecimal.valueOf(0.01),
            Payment(creditCard),
            poNumber,
        )

        val authentication = merchantAuthentication()

        val transactionRequest = CreateTransactionRequest(
            authentication,
            transactionObject
        )

        val responseBody = request(transactionRequest)

        val errosDeValidacao = verificaErrosDeValidacao(responseBody.transactionResponse
            ?: throw ExternalServiceException(mensagemErroPadrao))

        if(errosDeValidacao.isNotEmpty()) return CartaoDeCreditoValidacaoStatus(false, errosDeValidacao)

        anularTransacao(responseBody.transactionResponse.transId ?:
            throw ExternalServiceException(mensagemErroPadrao))

        return CartaoDeCreditoValidacaoStatus(true)
    }

    override fun enviarCobranca(
        valor: BigDecimal,
        cartaoDeCredito: CartaoDeCredito,
        ciclista: Ciclista,
    ): CartaoDeCreditoCobrancaStatus {
        val creditCard = CreditCard(cartaoDeCredito)
        val documento = ciclista.cpf ?: ciclista.passaporte?.numero ?: "000000"
        val poNumber = documento.substring(0, 6) + Instant.now().nano
        val customer = Customer(ciclista.id.toString(), CustomerType.INDIVIDUAL, ciclista.email)

        val transactionObject = TransactionRequest(
            TransactionType.AUTH_CAPTURE_TRANSACTION,
            valor,
            Payment(creditCard),
            poNumber,
            customer
        )

        val authentication = merchantAuthentication()

        val transactionRequest = CreateTransactionRequest(
            authentication,
            transactionObject
        )

        val responseBody = request(transactionRequest)

        val errosDeValidacao = verificaErrosDeValidacao(responseBody.transactionResponse
            ?: throw ExternalServiceException(mensagemErroPadrao))

        if(errosDeValidacao.isNotEmpty()) return CartaoDeCreditoCobrancaStatus(StatusPagamentoEnum.FALHA, errosDeValidacao)

        return CartaoDeCreditoCobrancaStatus(StatusPagamentoEnum.PAGA)
    }

    private fun anularTransacao(transId: String) {
        val transactionObject = TransactionRequest(
            TransactionType.VOID_TRANSACTION,
            refTransId = transId
        )

        val merchantAuthentication = merchantAuthentication()

        val transactionRequest = CreateTransactionRequest(
            merchantAuthentication,
            transactionObject,
        )

        request(transactionRequest)
    }

    private fun request(transactionRequest: CreateTransactionRequest): CreateTransactionResponse {
        val requestBody = CreateTransactionRequestWrapped(transactionRequest)
        val response = restTemplate.postForEntity(authorizeNetConfig.url, requestBody, CreateTransactionResponse::class.java)

        if (response.statusCode != HttpStatus.OK) throw ExternalServiceException(
            mensagemErroPadrao + " HTTP Status code inesperado: ${response.statusCode}"
        )

        val responseBody = response.body ?: throw ExternalServiceException(mensagemErroPadrao)

        return responseBody
    }

    private fun verificaErrosDeValidacao(transactionResponse: TransactionResponse) : List<String> {
        val erroList = mutableListOf<String>()
        if (transactionResponse.responseCode != "1")
            erroList.add("Cartao de credito invalido")

        if (transactionResponse.cvvResultCode != "" && transactionResponse.cvvResultCode != "M")
            erroList.add("CVV invalido. Authorize.Net")

        if (!transactionResponse.errors.isNullOrEmpty()) {
            if (transactionResponse.errors.any { it?.errorCode == "8" })
                erroList.add("Cartao de credito venciado")
            else erroList.add("Operacao mal-sucedida. Consulte a operadora para obter mais detalhes")
        }

        return erroList.toList()
    }

    private fun merchantAuthentication(): MerchantAuthentication {
        return MerchantAuthentication(
            authorizeNetConfig.id,
            authorizeNetConfig.key
        )
    }
}